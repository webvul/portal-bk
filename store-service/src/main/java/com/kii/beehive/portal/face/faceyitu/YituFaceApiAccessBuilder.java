package com.kii.beehive.portal.face.faceyitu;

import java.util.HashMap;
import java.util.Map;
import org.apache.http.client.methods.HttpUriRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kii.beehive.portal.face.entitys.FaceImage;
import com.kii.beehive.portal.face.faceyitu.entitys.YituFaceImage;
import com.kii.beehive.portal.helper.HttpInvokeBuilder;

@Component
public class YituFaceApiAccessBuilder {



	@Autowired
	private ObjectMapper objectMapper;

	@Value("${yitu.url}")
	private String baseUrl;
	@Value("${yitu.username}")
	private String faceUsername;
	@Value("${yitu.password}")
	private String facePassword;

	private Logger log= LoggerFactory.getLogger(YituFaceApiAccessBuilder.class);
	
	public YituFaceApiAccessBuilder() {
	}

	public YituFaceApiAccessBuilder(ObjectMapper objectMapper, String baseUrl, String faceUsername, String facePassword) {
		this.objectMapper = objectMapper;
		this.baseUrl = baseUrl;
		this.faceUsername = faceUsername;
		this.facePassword = facePassword;
	}

	public HttpUriRequest buildLogin() {

		String fullUrl = baseUrl + ":7500/resource_manager/user/login";

		Map<String, Object> json = new HashMap<>();
		String body = null;
		try {
			json.put("name", faceUsername);
			json.put("password", facePassword);
			body = objectMapper.writeValueAsString(json);
		} catch (JsonProcessingException e) {
			log.error(e.getMessage());
		}
		HttpUriRequest invoke = new HttpInvokeBuilder().setUrl(fullUrl)
				.buildCustomCall("post", body).generRequest(objectMapper);

		invoke.setHeader("Content-Type", "application/json");

		return invoke;
	}


	public HttpUriRequest buildUploadImage(YituFaceImage faceImage) {

		String fullUrl = baseUrl + ":9100/face/v1/framework/face_image/repository/picture/synchronized";

		String body = null;
		try {
			body = objectMapper.writeValueAsString(faceImage);
		} catch (JsonProcessingException e) {
			log.error(e.getMessage());
		}
		HttpUriRequest invoke = new HttpInvokeBuilder().setUrl(fullUrl)
				.buildCustomCall("post", body).generRequest(objectMapper);

		return invoke;
	}

	/**
	 * register
	 * @return
	 */
	public HttpUriRequest buildDeleteImage(FaceImage faceImage) {

		String fullUrl = baseUrl + ":9200/face/v1/framework/face/delete";

		String body = null;
		try {
			body = objectMapper.writeValueAsString(faceImage);
		} catch (JsonProcessingException e) {
			log.error(e.getMessage());
		}
		HttpUriRequest invoke = new HttpInvokeBuilder().setUrl(fullUrl)
				.buildCustomCall("post", body).generRequest(objectMapper);

		return invoke;
	}
	/**
	 * buildGuestCheckin
	 * @return
	 */
	public HttpUriRequest buildGuestCheckin(Map<String, Object> postData) {

		String fullUrl = baseUrl + ":58147/guest/checkin";

		String body = null;
		try {
			body = objectMapper.writeValueAsString(postData);
		} catch (JsonProcessingException e) {
			log.error(e.getMessage());
		}
		HttpUriRequest invoke = new HttpInvokeBuilder().setUrl(fullUrl)
				.buildCustomCall("post", body).generRequest(objectMapper);

		return invoke;
	}



	public HttpUriRequest buildGetRepository() {

		String fullUrl = baseUrl + ":11180/face/v1/framework/face_image/repository";

		HttpUriRequest invoke = new HttpInvokeBuilder().setUrl(fullUrl)
				.buildCustomCall("post", null).generRequest(objectMapper);

		return invoke;
	}



}
