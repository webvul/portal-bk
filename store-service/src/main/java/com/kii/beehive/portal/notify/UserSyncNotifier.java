package com.kii.beehive.portal.notify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kii.beehive.portal.service.DeviceSupplierDao;
import com.kii.beehive.portal.service.NotifyUserFailureDao;
import com.kii.beehive.portal.store.entity.NotifyUserFailure;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


@Component
public class UserSyncNotifier {

    // create user
    public static final String CHANGE_TYPE_CREATE = "create";

    // update user
    public static final String CHANGE_TYPE_UPDATE = "update";

    // delete user
    public static final String CHANGE_TYPE_DELETE = "delete";

    // retry count when failed to call the url
    private static final int URL_RETRY_COUNT = 2;

    // retry internal increase when failed to call the url
    private static final long URL_RETRY_INTERVAL_INCREASE = 3000;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd hhmmss");

    private Logger logger;

    private ThreadPoolExecutor executor;

    // map between device supplier ID and userInfoNotifyUrl
    private Map<String, String> userInfoNotifyUrlMap;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DeviceSupplierDao supplierDao;

    @Autowired
    private NotifyUserFailureDao notifyUserFailureDao;

    @PostConstruct
    public void init() {

        logger = Logger.getLogger(this.getClass());

        executor = new ThreadPoolExecutor(8, 64, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

        userInfoNotifyUrlMap = new ConcurrentHashMap<String, String>();
        supplierDao.getAllSupplier().stream().forEach((entity) -> {
            userInfoNotifyUrlMap.put(entity.getId(), entity.getUserInfoNotifyUrl());
        });

        logger.info("loaded user info notify url:" + userInfoNotifyUrlMap);

    }

    /**
     * notify each device supplier system of the user info change in async way
     * except for the device supplier specified by param sourceParty3rdID, all other device suppliers will be notified,
     * if sourceParty3rdID is null, all device suppliers will be notified
     *
     * @param sourceParty3rdID      device supplier ID who made the user info change
     * @param beehiveUserID ID of the changed user
     * @param changeType    change type,
     *                      CHANGE_TYPE_CREATE: user created,
     *                      CHANGE_TYPE_UPDATE: user info updated,
     *                      CHANGE_TYPE_DELETE: user deleted
     */
    public void notifyDeviceSuppliersAsync(String sourceParty3rdID, String beehiveUserID, String changeType) {

        logger.debug("Start notifyDeviceSuppliersAsync(String sourceParty3rdID, String beehiveUserID, String changeType)");
        logger.debug("sourceParty3rdID:" + sourceParty3rdID);
        logger.debug("beehiveUserID:" + beehiveUserID);
        logger.debug("changeType:" + changeType);

        List<String> userIdList = new ArrayList<String>();
        userIdList.add(beehiveUserID);

        this.notifyDeviceSuppliersAsync(sourceParty3rdID, userIdList, changeType);

        logger.debug("End notifyDeviceSuppliersAsync(String sourceParty3rdID, String beehiveUserID, String changeType)");

    }

    /**
     * notify each device supplier system of the user info change in async way,
     * except for the device supplier specified by param sourceParty3rdID, all other device suppliers will be notified,
     * if sourceParty3rdID is null, all device suppliers will be notified
     *
     * @param sourceParty3rdID device supplier ID who made the user info change
     * @param beehiveUserIDList ID list of the changed users
     * @param changeType        change type,
     *                          CHANGE_TYPE_CREATE: user created,
     *                          CHANGE_TYPE_UPDATE: user info updated,
     *                          CHANGE_TYPE_DELETE: user deleted
     */
    public void notifyDeviceSuppliersAsync(String sourceParty3rdID, List<String> beehiveUserIDList, String changeType) {

        logger.debug("Start notifyDeviceSuppliersAsync(String sourceParty3rdID, List<String> beehiveUserIDList, String changeType)");
        logger.debug("sourceParty3rdID:" + sourceParty3rdID);
        logger.debug("beehiveUserIDList:" + beehiveUserIDList);
        logger.debug("changeType:" + changeType);

        try {

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("user_id", beehiveUserIDList);
            map.put("change_type", changeType);

            String notifyContent = objectMapper.writeValueAsString(map);

            // start new threads to call the url of each device supplier
            userInfoNotifyUrlMap.forEach((party3rdID, url) -> {
                if (!party3rdID.equals(sourceParty3rdID)) {
                    notifyInThread(party3rdID, notifyContent, url);
                }
            });

        } catch (Exception e) {
            logger.error(e);
        }

        logger.debug("End notifyDeviceSuppliersAsync(String sourceParty3rdID, List<String> beehiveUserIDList, String changeType)");

    }

    /**
     * start new thread to call the url of each device supplier,
     * there would be 3 retries and the retry internal increased in case of failing to call the url,
     * if all retries failed, store the failure info into Beehive DB
     *
     * @param destParty3rdID
     * @param notifyContent
     * @param userInfoNotifyUrl
     */
    private void notifyInThread(String destParty3rdID, String notifyContent, String userInfoNotifyUrl) {

        executor.execute(new Runnable() {
            @Override
            public void run() {

                logger.debug("Start thread to notify");
                logger.debug("destParty3rdID:" + destParty3rdID);
                logger.debug("notifyContent:" + notifyContent);
                logger.debug("userInfoNotifyUrl:" + userInfoNotifyUrl);

                try {
                    HttpPost method = new HttpPost(userInfoNotifyUrl);

                    StringEntity entity = new StringEntity(notifyContent, "UTF-8");
                    entity.setContentEncoding("UTF-8");
                    entity.setContentType("application/json");
                    method.setEntity(entity);

                    boolean notifyResult = notifySingle(method);

                    // if failed to call the url, start to retry
                    if (notifyResult == false) {

                        long interval = 0;
                        for (int i = 0; i < URL_RETRY_COUNT; i++) {

                            interval += URL_RETRY_INTERVAL_INCREASE;
                            Thread.sleep(interval);

                            notifyResult = notifySingle(method);

                            // if retry succeeds, break
                            if (notifyResult == true) {
                                break;
                            }
                        }

                        // if all retries failed, store the failure info into Beehive DB
                        if (notifyResult == false) {
                            StringBuffer logContent = new StringBuffer("failed to notify device supplier, store failure info to Beehive DB -");
                            logContent.append(" party3rdID:").append(destParty3rdID);
                            logContent.append(" notifyContent:").append(notifyContent);
                            logContent.append(" userInfoNotifyUrl:").append(userInfoNotifyUrl);

                            logger.error(logContent.toString());

                            storeNotifyFailure(destParty3rdID, notifyContent, userInfoNotifyUrl);
                        }
                    }
                } catch (Exception e) {
                    logger.error(e);
                }

                logger.debug("End thread to notify");
            }
        });

    }

    /**
     * call the url of device supplier
     *
     * @param method
     * @return
     */
    private boolean notifySingle(HttpPost method) {
        try {
            HttpClient httpClient = HttpClients.createDefault();

            HttpResponse result = httpClient.execute(method);

            int statusCode = result.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                throw new IOException("http status code:" + statusCode);
            }
            return true;
        } catch (IOException e) {
            logger.warn("failed to notify:" + method, e);
            return false;
        }
    }

    /**
     * store the failure info into Beehive DB
     *
     * @return
     */
    private void storeNotifyFailure(String destParty3rdID, String notifyContent, String userInfoNotifyUrl) {

        NotifyUserFailure notifyUserFailure = new NotifyUserFailure();

        notifyUserFailure.setParty3rdID(destParty3rdID);
        notifyUserFailure.setUrlNotifyUser(userInfoNotifyUrl);
        notifyUserFailure.setPostContent(notifyContent);
        notifyUserFailure.setFailureTime(DATE_FORMAT.format(new Date()));

        notifyUserFailureDao.addNotifyUserFailure(notifyUserFailure);
    }

}
