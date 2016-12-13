package com.kii.beehive.portal.face.faceyitu;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.HttpUriRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kii.beehive.portal.face.FaceServiceInf;
import com.kii.beehive.portal.face.entitys.FaceImage;
import com.kii.beehive.portal.face.faceyitu.entitys.YituFaceImage;
import com.kii.beehive.portal.helper.HttpClient;
import com.kii.beehive.portal.jdbc.dao.BeehiveUserJdbcDao;
import com.kii.beehive.portal.jedis.dao.MessageQueueDao;


/**
 *
 */
@Component
public class YituFaceService implements FaceServiceInf {

	@Autowired
	private HttpClient httpClient;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private BeehiveUserJdbcDao beehiveUserJdbcDao;

	@Autowired
	private MessageQueueDao messageQueueDao;

	@Autowired
	private YituFaceApiAccessBuilder yituFaceApiAccessBuilder;

	private Logger log = LoggerFactory.getLogger(YituFaceService.class);

	private List<String> cookieList = new ArrayList<>();

	@PostConstruct
	public void init() throws JsonProcessingException, URISyntaxException {
		loginServer();
	}

	@Scheduled(cron = "0 0/5 * * * ?")
	public void loginServer() {

		HttpUriRequest faceRequest = yituFaceApiAccessBuilder.buildLogin();
		log.debug("Start login to face:" + faceRequest.getURI());

		String responseBody = null;
		try {

			responseBody = httpClient.executeRequest(faceRequest);

			Map<String, Object> result = objectMapper.readValue(responseBody, new TypeReference<HashMap>() {});
			if (result != null && result.get("rtn") != null && !result.get("rtn").toString().equals("0")) {
				log.error("login to face yitu failed, result:" + result);
				return;
			}

			cookieList.clear();
			cookieList.add("session_id=" + result.get("session_id"));

			log.info("yitu face cookie :" + result.get("session_id"));

		} catch (Exception e) {
			log.error(e.getMessage());
		}

	}

	public FaceImage doUploadImage(File imageFile) {

		YituFaceImage yituFaceImage = new YituFaceImage();

		InputStream in = null;
		byte[] data = null;
		try {
			in = new FileInputStream(imageFile);
			data = new byte[in.available()];
			in.read(data);
			in.close();
		} catch (IOException e) {
			log.error(e.getMessage());
		}

		yituFaceImage.setPicture_image_content_base64(Base64.encodeBase64String(data));
		return doUploadImage(yituFaceImage);
	}

	public FaceImage doUploadImage(YituFaceImage yituFaceImage) {

		String responseBody = null;

		HttpUriRequest faceRequest = yituFaceApiAccessBuilder.buildUploadImage(yituFaceImage);
		faceRequest.setHeader("cookie", cookieList.get(0));
		log.debug("upload photo :" + faceRequest.getURI());
		responseBody = httpClient.executeRequest(faceRequest);

		log.debug(responseBody);
		Map<String, Object> result = null;
		try {
			result = objectMapper.readValue(responseBody, new TypeReference<HashMap>() {
			});
		} catch (IOException e) {
			throw new FaceYituException(e);
		}
		FaceImage faceImage = null;
		if(result.get("rtn").toString().equals("0")){
			faceImage = new FaceImage();
			Long face_image_id =  Long.valueOf ( ( (List<Map<String, Object>>)result.get("results") ).get(0).get("face_image_id").toString() );
			faceImage.setFace_image_id(face_image_id);
		}

		return faceImage;
	}

	//register
	public boolean doDeleteImage(String id) {
		boolean rtn = false;
		FaceImage faceImage = new FaceImage();
		faceImage.setFace_image_id(Long.valueOf(id));
		HttpUriRequest faceRequest = yituFaceApiAccessBuilder.buildDeleteImage(faceImage);
		faceRequest.setHeader("cookie", cookieList.get(0));
		log.debug("doDeleteImage :" + faceRequest.getURI());
		String responseBody = null;
		responseBody = httpClient.executeRequest(faceRequest);
		log.debug("doDeleteImage \n" + responseBody);
		Map<String, Object> result = null;
		try {
			result = objectMapper.readValue(responseBody, new TypeReference<HashMap>() {});
		} catch (IOException e) {
			log.error(responseBody);

		}
		if(result.get("rtn").toString().equals("0")){
			rtn = true;
		}
		else if(result.get("rtn").toString().equals("-10037")){
			throw new FaceYituException();
		}
		return rtn;
	}



	public void doGetRepository() {

		HttpUriRequest faceRequest = yituFaceApiAccessBuilder.buildGetRepository();
		faceRequest.setHeader("cookie", cookieList.get(0));
		log.debug("buildGetRepository :" + faceRequest.getURI());
		String responseBody = null;
		responseBody = httpClient.executeRequest(faceRequest);

		log.debug("buildGetRepository: \n" + responseBody);

	}


}
