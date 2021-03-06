package com.kii.beehive.portal.face.faceplusplus;

import javax.annotation.PostConstruct;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.portal.face.faceplusplus.entitys.FacePlusPlusUser;
import com.kii.beehive.portal.helper.HttpClient;
import com.kii.beehive.portal.jdbc.dao.BeehiveUserJdbcDao;
import com.kii.beehive.portal.jdbc.entity.BeehiveJdbcUser;
import com.kii.beehive.portal.jedis.dao.MessageQueueDao;
import com.kii.beehive.portal.sysmonitor.SysMonitorMsg;
import com.kii.beehive.portal.sysmonitor.SysMonitorQueue;

import io.socket.client.IO;
import io.socket.client.Manager;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.Transport;


/**
 *
 */
@Component
public class FacePlusPlusService {

    @Autowired
    private HttpClient httpClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BeehiveUserJdbcDao beehiveUserJdbcDao;

    @Autowired
    private MessageQueueDao messageQueueDao;

    @Autowired
    private FacePlusPlusApiAccessBuilder facePlusPlusApiAccessBuilder;

    @Value("${face.web_socket.uri}")
    private String faceWebSocketUri;
    @Value("${face.web_socket.queue:faceplusplus}")
    private String faceQueue;

    private Logger log = LoggerFactory.getLogger(FacePlusPlusService.class);

    private Logger faceLog = LoggerFactory.getLogger("faceplusplus");

    private Socket socket = null;

    private List<String> cookieList = new ArrayList<>();
	@Value("${spring.profile}")
	private String profile;

    @PostConstruct
    public void init() throws JsonProcessingException, URISyntaxException {

        loginServer();
        startConnection();

    }

    public Map<String, Object> buildUploadPhoto(File photoFile) {
        if(photoFile == null)
            throw new RuntimeException("photo file can not null!");

        String responseBody = null;
        HttpUriRequest faceRequest = facePlusPlusApiAccessBuilder.buildUploadPhoto(photoFile);
        faceRequest.setHeader("cookie", cookieList.get(0));
        log.debug("upload photo :" + faceRequest.getURI());
        responseBody = httpClient.executeRequest(faceRequest);
        log.debug(responseBody);
        Map<String, Object> result = null;
        try {
            result = objectMapper.readValue(responseBody, new TypeReference<HashMap>() {});
        } catch (IOException e) {
            throw new FacePlusPlusException(e);
        }
        return result;
    }

    public List<Map<String, Object>> buildUploadPhotos(List<File> photoFiles) {
        List<Map<String, Object>> result = new ArrayList<>();
        photoFiles.forEach( photoFile -> result.add(buildUploadPhoto(photoFile)));
        return result;
    }

    //register
    public Map<String, Object> buildSubject(FacePlusPlusUser faceUser) {

        HttpUriRequest faceRequest = facePlusPlusApiAccessBuilder.buildSubject(faceUser);
        faceRequest.setHeader("cookie", cookieList.get(0));//*
        log.debug("buildSubject :" + faceRequest.getURI());
        String responseBody = null;
        responseBody = httpClient.executeRequest(faceRequest);
        log.debug("register face++ \n" + responseBody);
        Map<String, Object> result = null;
        try {
            result = objectMapper.readValue(responseBody, new TypeReference<HashMap>() {});
        } catch (IOException e) {
            throw new FacePlusPlusException(e);
        }
        return result;
    }
	
	//update
    public Map<String, Object> buildUpdateSubject(FacePlusPlusUser faceUser) {

        HttpUriRequest faceRequest = facePlusPlusApiAccessBuilder.buildUpdateSubject(faceUser);
        faceRequest.setHeader("cookie", cookieList.get(0));//*
        log.debug("buildUpdateSubject :" + faceRequest.getURI());
        String responseBody = null;
        responseBody = httpClient.executeRequest(faceRequest);
        log.debug("update face++ \n" + responseBody);
        Map<String, Object> result = null;
        try {
            result = objectMapper.readValue(responseBody, new TypeReference<HashMap>() {});
        } catch (IOException e) {
            throw new FacePlusPlusException(e);
        }
        return result;
    }

	/**
	 * get face++ user by id
     * @param id
     */
    public Map<String, Object> buildGetSubjectById(Integer id) {

        HttpUriRequest faceRequest = facePlusPlusApiAccessBuilder.buildGetSubjectById(id);
        faceRequest.setHeader("cookie", cookieList.get(0));
        log.debug("buildGetSubjectById :" + faceRequest.getURI());
        String responseBody = null;
        responseBody = httpClient.executeRequest(faceRequest);
        log.debug("buildGetSubjectById: \n" + responseBody);
        Map<String, Object> result = null;
        try {
            result = objectMapper.readValue(responseBody, new TypeReference<HashMap>() {});
        } catch (IOException e) {
			throw new FacePlusPlusException(e);
        }
        return result;
    }

    @Deprecated
    public void buildGetSubjects() {

        HttpUriRequest faceRequest = facePlusPlusApiAccessBuilder.buildGetSubjects();
        faceRequest.setHeader("cookie", cookieList.get(0));
        log.debug("buildGetSubjects :" + faceRequest.getURI());
        String responseBody = null;
        responseBody = httpClient.executeRequest(faceRequest);
        log.debug("buildGetSubjects: \n" + responseBody);

    }

    @Scheduled(cron = "0 0/5 * * * ?")
    protected void loginServer() {

        HttpUriRequest faceRequest = facePlusPlusApiAccessBuilder.buildLogin();
        log.debug("Start login to face:" + faceRequest.getURI());

        String responseBody = null;
        try {
            HttpClientContext context = HttpClientContext.create();
            responseBody = httpClient.executeRequest(faceRequest, context);

            log.debug(responseBody);

            Map<String, Object> result = objectMapper.readValue(responseBody, new TypeReference<HashMap>() {});
            if (result != null && result.get("code") != null && ! result.get("code").toString().equals("0") ) {
                log.error("Login to face++ failed, code:" + result);
                return;
            }

            CookieStore cookieStore = context.getCookieStore();
            cookieList.clear();
            List<Cookie> cookies = cookieStore.getCookies();
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("session")) {
                    cookieList.add(cookie.getName() + "=" + cookie.getValue());
//                    this.cookie = cookie;
                }
            }

            log.info("face++ cookie :" + cookieList);

        } catch (Exception e) {
			log.error(e.getMessage());
			log.error(e.getMessage());
			SysMonitorMsg notice = new SysMonitorMsg();
			notice.setFrom(SysMonitorMsg.FromType.FacePlusPlus);
			notice.setErrMessage(e.getMessage());
			notice.setErrorType("LoginFacePlusPlusFail");
			SysMonitorQueue.getInstance().addNotice(notice);
		}


    }

    @Scheduled(cron = "0 0/5 * * * ?")
    public void reConnectWebSocket(){
        if( ! socket.connected() ) {
            log.info("face++ reConnectWebSocket...");
            socket.connect();
        }
    }

    protected void startConnection() throws URISyntaxException {
        IO.Options options = new IO.Options();
        
        options.reconnectionDelay=1000l;
        if("local".equals(profile)){
        	options.reconnection=false;
		}
        options.transports = new String[]{"websocket"};
        socket = IO.socket(faceWebSocketUri + "/event/", options);
        

        socket.io().on(Manager.EVENT_TRANSPORT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Transport transport = (Transport) args[0];
                transport.on(Transport.EVENT_REQUEST_HEADERS, new
                        Emitter.Listener() {
                            @Override
                            public void call(Object... args) {
                                @SuppressWarnings("unchecked")
                                Map<String, List<String>> headers = (Map<String,
                                        List<String>>) args[0];
                                // modify request headers
                                headers.put("Cookie", cookieList);
                                log.debug("SocketIO to face++ get header");
                            }
                        });
            }
        });

        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("employee", true);
                    obj.put("visitor", true);
                    obj.put("vip", true);
                    obj.put("stanger", true);
                } catch (JSONException e) {
					log.error(e.getMessage(),e);
                }
                socket.emit("subscribe", obj);
                log.info("face++ websocket Connected to Face++ WebSocket");
            }
        }).on("event", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                log.info("face++ websocket event ****************");
                String eventJsonStr = "";
                if(args.length > 0 && args[0] != null){
                    eventJsonStr = args[0].toString();
                    String postEventJsonStr = eventJsonStr;
                    Map<String, Object> eventResult = null;
                    try {
                        eventResult = objectMapper.readValue(eventJsonStr, new TypeReference<HashMap>() {});

                        String faceUserID = eventResult.get("subject_id") == null ? "" : String.valueOf(eventResult.get("subject_id"));
                        if( ! StringUtils.isEmpty(faceUserID) ){
                            BeehiveJdbcUser user = beehiveUserJdbcDao.getUserByFaceUserID(faceUserID);
                            if(user != null) {
                                eventResult.put("beehive_user_id", user.getUserID());
                            }
                        }

                        postEventJsonStr = objectMapper.writeValueAsString(eventResult);
                        faceLog.info(postEventJsonStr);
                    } catch (IOException e) {
                        log.error(e.getMessage(),e);
                    }

                    //push redis
                    log.info("face++ websocket received postEventJsonStr:" + postEventJsonStr);
                    messageQueueDao.lpush(faceQueue, postEventJsonStr);
                }
            }
        }).on("join", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                log.info("face++ websocket join ****************");
            }
        }) .on(Socket.EVENT_RECONNECT_FAILED, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                log.error("face++ websocket EVENT_RECONNECT_FAILED _event");
                for (Object arg : args) {
                    log.error("EVENT_RECONNECT_FAILED : " + arg);
                }
                loginServer();
            }
        }) .on(Socket.EVENT_RECONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                log.error("face++ websocket EVENT_RECONNECT_ERROR _event");
                for (Object arg : args) {
                    log.error("EVENT_RECONNECT_ERROR : " + arg);
                }
                loginServer();
            }
        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                log.error("face++ websocket disconnect");
            }
        });
        socket.connect();
    }

}
