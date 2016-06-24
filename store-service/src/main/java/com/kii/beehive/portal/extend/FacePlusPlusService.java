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

//        {
//            FaceUser faceUser = new FaceUser();
//            faceUser.setSubject_type(0);
//            faceUser.setName("beehive_user_photoId:no-" + new Random().nextInt(1000));
//            buildSubject(faceUser);
//        }
///*
        for (int i = 0; i < 0; i++) {

            List<Integer> photos = new ArrayList<>();
            Integer anyPhotoId = null;
            for (int j = 1; j < 11; j++) {
//                File photoFile = new File("/Users/user/Downloads/"+j+".jpg");
                File photoFile = new File("/Users/user/Downloads/alan2.jpg");
//                photoFile = null;
                Map<String, Object> photoMap = buildUploadPhoto(photoFile);
                Integer photoId = (Integer) ( (Map<String, Object>)photoMap.get("data") ).get("id");
                photos.add(photoId);
                anyPhotoId = photoId;
            }

            FaceUser faceUser = new FaceUser();
            faceUser.setSubject_type(0);
            faceUser.setName("beehive_user_photoId:" + anyPhotoId);
            faceUser.setPhoto_ids(photos);
            Map<String, Object> userMap = buildSubject(faceUser);
            Integer userId = (Integer) ( (Map<String, Object>)userMap.get("data") ).get("id");
//            //
//            photoMap = buildUploadPhoto(null);
//            photoId = (Integer) ( (Map<String, Object>)photoMap.get("data") ).get("id");
//            //
//            faceUser = new FaceUser();
//            faceUser.setId(userId);
//            faceUser.setPhoto_ids(Arrays.asList(photoId));
//            buildUpdateSubject(faceUser);


            //用相同的photoId,api是过的 产生的新用户 而且 返回值里面是有photoId列表的,
            //但是好像在https://v2.koalacam.net/subject/employee 这个网站里面看不到识别照片
            //获取用户的api列表的里面 这种用户没有photoId 【photo_ids属性为空】
//            faceUser.setName(faceUser.getName()+"-2");
//            buildSubject(faceUser);

        }

//        buildGetSubjects();
//*/

    }


    public Map<String, Object> buildUploadPhoto(File photoFile) {
        if(photoFile == null)
            photoFile = new File("/Users/user/Downloads/2.jpg");

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
    //register
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
