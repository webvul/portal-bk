package com.kii.beehive.portal.extend;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kii.beehive.portal.extend.entitys.FaceUser;
import com.kii.beehive.portal.helper.HttpClient;


/**
 *
 */
@Service
public class FacePlusPlusService {

    @Autowired
    private HttpClient httpClient;
//    private KiiCloudClient httpClient; //UnsupportedOperationException

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FacePlusPlusApiAccessBuilder facePlusPlusApiAccessBuilder;



    private Logger log = LoggerFactory.getLogger(FacePlusPlusService.class);

    List<String> cookieList = new ArrayList<String>();
//    Cookie cookie;

    @PostConstruct
    public void init() throws JsonProcessingException {
        loginServer();


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
            e.printStackTrace();
        }
        return result;
    }

    public List<Map<String, Object>> buildUploadPhotos(List<File> photoFiles) {
        List<Map<String, Object>> result = new ArrayList<>();
        photoFiles.forEach( photoFile -> result.add(buildUploadPhoto(photoFile)));
        return result;
    }

    //register
    public Map<String, Object> buildSubject(FaceUser faceUser) {

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
            e.printStackTrace();
        }
        return result;
    }
    //update
    public Map<String, Object> buildUpdateSubject(FaceUser faceUser) {

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
            e.printStackTrace();
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
            e.printStackTrace();
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

            log.error("face++ cookie :" + cookieList);

        } catch (Exception e) {
            log.error(e.getMessage());
        }


    }



}
