package com.kii.beehive.portal.extend;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.client.methods.HttpUriRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kii.beehive.portal.extend.entitys.FaceUser;
import com.kii.beehive.portal.helper.HttpInvokeBuilder;

@Service
public class FacePlusPlusApiAccessBuilder {



	@Autowired
	private ObjectMapper objectMapper;

	@Value("${face.web_socket.uri}")
	private String baseUrl;
	@Value("${face.web_socket.username}")
	private String faceUsername;
	@Value("${face.web_socket.password}")
	private String facePassword;

	public FacePlusPlusApiAccessBuilder() {

	}
	@Deprecated
	public FacePlusPlusApiAccessBuilder(String baseUrl, ObjectMapper objectMapper) {

		this.baseUrl = baseUrl;
		this.objectMapper = objectMapper;
	}

	public HttpUriRequest buildLogin() {

		String fullUrl = baseUrl + "/auth/login";

		Map<String, Object> json = new HashMap<>();
		String body = null;
		try {
			json.put("username", faceUsername);
			json.put("password", facePassword);
			body = objectMapper.writeValueAsString(json);
		} catch (Exception e1) {
		}
		HttpUriRequest invoke = new HttpInvokeBuilder().setUrl(fullUrl)
				.buildCustomCall("post", body).generRequest(objectMapper);

		invoke.setHeader("user-agent", "Koala Admin");
		invoke.setHeader("Content-Type", "application/json");

		return invoke;
	}


	public HttpUriRequest buildUploadPhoto(File photoFile) {

		String fullUrl = baseUrl + "/subject/photo";

		HttpUriRequest invoke = new HttpInvokeBuilder().setUrl(fullUrl)
				.buildCustomCall("post", photoFile).setFileName("photo")
				.clearContentType().generRequest(objectMapper);

		return invoke;
	}

	/**
	 * register
	 * @return
	 */
	public HttpUriRequest buildSubject(FaceUser faceUser) {

		String fullUrl = baseUrl + "/subject";

		String body = null;
		try {
			body = objectMapper.writeValueAsString(faceUser);
		} catch (Exception e1) {
		}
		HttpUriRequest invoke = new HttpInvokeBuilder().setUrl(fullUrl)
				.buildCustomCall("post", body).generRequest(objectMapper);

		return invoke;
	}

	/**
	 * update user
	 * @return
	 */
	public HttpUriRequest buildUpdateSubject(FaceUser faceUser) {

		String fullUrl = baseUrl + "/subject/" + faceUser.getId();

		String body = null;
		try {
			body = objectMapper.writeValueAsString(faceUser);
		} catch (Exception e1) {
		}
		HttpUriRequest invoke = new HttpInvokeBuilder().setUrl(fullUrl)
				.buildCustomCall("put", body).generRequest(objectMapper);

		return invoke;
	}

	/**
	 * get face++ user by id
	 * @return
	 */
	public HttpUriRequest buildGetSubjectById(Integer id) {

		String fullUrl = baseUrl + "/subject/" + id;

		HttpUriRequest invoke = new HttpInvokeBuilder().setUrl(fullUrl)
				.buildCustomCall("get", null).clearContentType().generRequest(objectMapper);

		return invoke;
	}

	/**
	 * get all face++ user
	 * @return
	 */
	public HttpUriRequest buildGetSubjects() {

		String fullUrl = baseUrl + "/mobile-admin/subjects";

		HttpUriRequest invoke = new HttpInvokeBuilder().setUrl(fullUrl)
				.buildCustomCall("get", null).generRequest(objectMapper);

		return invoke;
	}



}
