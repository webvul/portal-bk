package com.kii.beehive.portal.helper;

import javax.annotation.PostConstruct;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.common.utils.CollectUtils;
import com.kii.beehive.portal.jdbc.dao.AuthInfoDao;
import com.kii.beehive.portal.jdbc.entity.AuthInfo;

/**
 * this class stores the auth info entities in cache (private map) and provides below functions:
 * 1. manage(add/update/remove) the auth info in both DB and cache,
 * 2. try to remove the expired auth info by certain interval
 *
 * the cache is not thread safe yet, would need to add some synchronization work if required
 */
@Component
public class AuthInfoCacheService {

    private Logger log= LoggerFactory.getLogger(AuthInfoCacheService.class);

    // token expiration will be "TOKEN_VALID_TIME_IN_MILLISECOND" million seconds later
    // 1 hour
    private static final long TOKEN_VALID_TIME_IN_MILLISECOND = 60 * 60 * 1000;

    // 1 hour
    private static final long CHECK_CACHE_INTERVAL_IN_MILLISECOND = 60 * 60 * 1000;

    @Autowired
    private AuthInfoDao authInfoDao;

    private ScheduledExecutorService executeService = Executors.newSingleThreadScheduledExecutor();

    /**
     * auth info cache
     * key: token
     * value AuthInfo entity
     */
    private Map<String, AuthInfo> authInfoMap = new HashMap<>();

    @PostConstruct
    public void init() {
        // every "CHECK_CACHE_INTERVAL_IN_MILLISECOND" mins, check whether the auth info entities in cache expired,
        // if any expired auth info found, remove it from cache and DB
        Runnable checkCache = new Runnable() {
            @Override
            public void run() {

                log.debug("check Cache start");

                Collection<AuthInfo> authInfoCollection = authInfoMap.values();

                for(AuthInfo authInfo : authInfoCollection) {
                    if(isExpired(authInfo)) {
                        removeAuthInfo(authInfo);
                    }
                }

                log.debug("check Cache end");
            }
        };

        executeService.scheduleAtFixedRate(checkCache, CHECK_CACHE_INTERVAL_IN_MILLISECOND, CHECK_CACHE_INTERVAL_IN_MILLISECOND, TimeUnit.MILLISECONDS);
    }

    /**
     * get available auth info from cache (available = not expired)
     * if not existing, try to get from DB and save into cache
     *
     * @param token
     * @return if valid token, return the corresponding AuthInfo entity; otherwise, return null
     */
    public AuthInfo getAvailableAuthInfo(String token) {

        log.debug("cache token: " + token);

        // check auth info in cache
        AuthInfo authInfo = authInfoMap.get(token);

        if(authInfo != null) {
            // if the existing auth info in cache is expired, remove from cache and DB, then return null;
            // otherwise, return the auth info entity
            if(this.isExpired(authInfo)) {
                this.removeAuthInfo(authInfo);
                return null;
            } else {
                return authInfo;
            }
        }

        // check auth info in DB
        List<AuthInfo> userInfoList = authInfoDao.findBySingleField(AuthInfo.TOKEN, token);
        authInfo = CollectUtils.getFirst(userInfoList);

        if(authInfo != null) {
            // if the existing auth info in DB is expired, remove from DB, then return null;
            // otherwise, add into cache, then return the auth info entity
            if(this.isExpired(authInfo)) {
                this.removeAuthInfo(authInfo);
                return null;
            } else {
                authInfoMap.put(token, authInfo);
                return authInfo;
            }
        }

        return null;
    }

    /**
     * save auth info into DB and cache
     * if there is existing auth info in DB or cache, will update the token inside auth info
     * @param userID
     * @param token
     */
    public AuthInfo saveToken(String userID, String token) {

        log.debug("save token: " + token);

        // save auth info into DB
        List<AuthInfo> authInfoList = authInfoDao.findBySingleField(AuthInfo.USER_ID, userID);

        AuthInfo authInfo = CollectUtils.getFirst(authInfoList);
        // if the user doesn't have token, create the auth info, then set user id
        // otherwise, clear the old token from cache
        if(authInfo == null) {
            authInfo = new AuthInfo();
            authInfo.setUserID(userID);
        } else {
            String oldToken = authInfo.getToken();
            if(oldToken != null) {
                authInfoMap.remove(oldToken);
            }
        }
        // set new token and expire time
        authInfo.setToken(token);
        authInfo.setExpireTime(this.calculateExpireTime());

        // save auth into into DB
        long id = authInfoDao.saveOrUpdate(authInfo);
        authInfo.setId(id);

        // save auth info into cache
        authInfoMap.put(token, authInfo);

        return authInfo;
    }

    /**
     * search auth info from DB by token, then remove auth info from cache and DB
     * @param token
     */
    public void removeToken(String token){
        log.debug("remove token: " + token);

        List<AuthInfo> authInfoList = authInfoDao.findBySingleField(AuthInfo.TOKEN, token);

        AuthInfo authInfo = CollectUtils.getFirst(authInfoList);
        if(authInfo != null) {
            this.removeAuthInfo(authInfo);
        }
    }

    /**
     * check whether token expired
     * @param authInfo
     * @return
     */
    private boolean isExpired(AuthInfo authInfo) {

        Date expireTime = authInfo.getExpireTime();
        // permanent valid
        if(expireTime == null) {
            return false;
        }

        return System.currentTimeMillis() > expireTime.getTime();
    }

    /**
     * remove auth info from cache and DB
     * @param authInfo
     */
    private void removeAuthInfo(AuthInfo authInfo) {

        authInfoMap.remove(authInfo.getToken());
        authInfoDao.deleteByID(authInfo.getId());

    }

    private Date calculateExpireTime() {
        long time = System.currentTimeMillis() + TOKEN_VALID_TIME_IN_MILLISECOND;
        return new Date(time);
    }

}
