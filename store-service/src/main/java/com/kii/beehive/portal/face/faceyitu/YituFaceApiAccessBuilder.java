package com.kii.beehive.portal.face.faceyitu;

import java.util.HashMap;
import java.util.Map;
import org.apache.http.client.methods.HttpUriRequest;
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

	@Value("${yitu.uri:http://120.26.7.217}")
	private String baseUrl;
	@Value("${yitu.username:admin}")
	private String faceUsername;
	@Value("${yitu.password:21232f297a57a5a743894a0e4a801fc3}")
	private String facePassword;

	public YituFaceApiAccessBuilder() {

	}

	public HttpUriRequest buildLogin() {

		String fullUrl = baseUrl + ":7500/resource_manager/user/login";

		Map<String, Object> json = new HashMap<>();
		String body = null;
		try {
			json.put("name", faceUsername);
			json.put("password", facePassword);
			body = objectMapper.writeValueAsString(json);
		} catch (Exception e1) {
		}
		HttpUriRequest invoke = new HttpInvokeBuilder().setUrl(fullUrl)
				.buildCustomCall("post", body).generRequest(objectMapper);

//		invoke.setHeader("user-agent", "Koala Admin");
		invoke.setHeader("Content-Type", "application/json");

		return invoke;
	}


	public HttpUriRequest buildUploadImage(YituFaceImage faceImage) {

		String fullUrl = baseUrl + ":9100/face/v1/framework/face_image/repository/picture/synchronized";

		String body = null;
		try {
			body = objectMapper.writeValueAsString(faceImage);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
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
			e.printStackTrace();
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
