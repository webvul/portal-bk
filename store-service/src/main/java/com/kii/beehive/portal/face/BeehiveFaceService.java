package com.kii.beehive.portal.face;

import javax.annotation.PostConstruct;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Files;
import com.kii.beehive.portal.exception.EntryNotFoundException;
import com.kii.beehive.portal.face.entitys.FaceImage;
import com.kii.beehive.portal.face.faceplusplus.FacePlusPlusService;
import com.kii.beehive.portal.face.faceyitu.YituFaceApiAccessBuilder;
import com.kii.beehive.portal.face.faceyitu.YituFaceService;
import com.kii.beehive.portal.face.faceyitu.entitys.YituFaceImage;
import com.kii.beehive.portal.jdbc.entity.BeehiveJdbcUser;
import com.kii.beehive.portal.manager.BeehiveUserManager;

@Component
public class BeehiveFaceService implements ApplicationContextAware{

	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private BeehiveUserManager userManager;

//	@Autowired
//	private YituFaceService yituFaceService;
	@Autowired
	private FacePlusPlusService facePlusPlusService;

	@Value("${yitu.url}")
	private String yituBaseUrl;
	@Value("${yitu.username}")
	private String yituFaceUsername;
	@Value("${yitu.password}")
	private String yituFacePassword;
	@Value("${yitu.repository_id}")
	private String yituFaceRepository_id;

	@Value("${face.photo.dir:${user.home}/data/beehive/face/photo/}")
	private String facePhotoDir;
	private String facePhotoTempDir;

	private File photoDir;
	private File photoTempDir;

	@PostConstruct
	public void init() {
		//face photo dir
		photoDir = new File(facePhotoDir);
		if (!photoDir.exists()) {
			photoDir.mkdirs();
		}
		//temp file dir
		facePhotoTempDir = facePhotoDir + "/temp";
		photoTempDir = new File(facePhotoTempDir);
		if (!photoTempDir.exists()) {
			photoTempDir.mkdirs();
		}
	}

	private static ApplicationContext applicationContext;
	private static DefaultListableBeanFactory defaultListableBeanFactory;

	public File getPhotoTempDir() {
		return photoTempDir;
	}

//	private Map<String, FaceServiceInf> faceServiceMap;
	private Map<String, YituFaceService> yituFaceServiceMap = new HashMap<>();

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
//		yituFaceServiceMap = applicationContext.getBeansOfType(YituFaceService.class);
		ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;
		defaultListableBeanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();
		buildYituService();

	}

	private void buildYituService() {

		String[] yituBaseUrls = yituBaseUrl.split(",");
		String[] yituFaceUsernames = yituFaceUsername.split(",");
		String[] yituFacePasswords = yituFacePassword.split(",");
		String[] yituFaceRepository_ids = yituFaceRepository_id.split(",");
		for (int i = 0 ; i < yituBaseUrls.length ; i++){
			String yituBeanName = "yituFaceService" + i;
			buildYituService(yituBaseUrls[i], yituFaceUsernames[i], yituFacePasswords[i], yituFaceRepository_ids[i], yituBeanName);
		}

	}

	public void buildYituService(String yituBaseUrl, String yituFaceUsername, String yituFacePassword,String repository_id, String yituBeanName) {
		YituFaceApiAccessBuilder yituFaceApiAccessBuilder = new YituFaceApiAccessBuilder(objectMapper
				, yituBaseUrl, yituFaceUsername, yituFacePassword);
		BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(YituFaceService.class);
		beanDefinitionBuilder.setAutowireMode(AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE);
		beanDefinitionBuilder.addPropertyValue("yituFaceApiAccessBuilder", yituFaceApiAccessBuilder);
		beanDefinitionBuilder.addPropertyValue("repository_id", repository_id);
		defaultListableBeanFactory.registerBeanDefinition(yituBeanName,beanDefinitionBuilder.getRawBeanDefinition());
		yituFaceServiceMap.put(yituBeanName, (YituFaceService)applicationContext.getBean(yituBeanName));
	}

	public File createUserFaceTempFile(String userId, byte[] photo) throws IOException {
		File photoFile = File.createTempFile(userId + "-" + UUID.randomUUID() + "-", ".jpg", this.getPhotoTempDir());
		BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(photoFile));
		stream.write(photo);
		stream.close();
		return photoFile;
	}

	public BeehiveJdbcUser updateUserWithFace(String userId, File photoFile) {


		BeehiveJdbcUser user = userManager.getUserByUserID(userId);
		if (user == null) {
			throw EntryNotFoundException.userIDNotFound(userId);
		}
		//face++
		/*
		Map<String, Object> photoMap = facePlusPlusService.buildUploadPhoto(photoFile);
		List<Integer> photoIds = new ArrayList<>();
		Integer photoId = (Integer) ((Map<String, Object>) photoMap.get("data")).get("id");
		if (photoId == null) {
			throw new IllegalArgumentException("upload face++ photo error ! face++:" + photoMap);
		}
		photoIds.add(photoId);
		*/


		//yitu
		YituFaceImage yituFaceImage = new YituFaceImage();
		yituFaceImage.setBeehive_user_id(user.getUserID());
		yituFaceImage.setExternal_id(user.getUserID());
		yituFaceImage.setName(user.getUserName());
		InputStream in = null;
		byte[] data = null;
		try {
			in = new FileInputStream(photoFile);
			data = new byte[in.available()];
			in.read(data);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		yituFaceImage.setPicture_image_content_base64(Base64.encodeBase64String(data));

		yituFaceServiceMap.values().forEach(yituFaceService -> {
			FaceImage faceImage = yituFaceService.doUploadImage(yituFaceImage);
			if(faceImage == null) {
				throw new IllegalArgumentException("upload yitu photo error ! ");
			}
		});


		//KII store image
		try {
			Files.copy(photoFile, new File(facePhotoDir + user.getUserID() + ".jpg"));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("store photo error ! ");
		}


		//yitu // TODO: 17/1/5 为记录 每个依图server对应的 ID ,没法删除
//		if (! StringUtils.isEmpty(user.getYituFaceImageId())) {
//			yituFaceService.doDeleteImage(user.getYituFaceImageId());
//		}
//		user.setYituFaceImageId(String.valueOf(faceImage.getFace_image_id()));

		//face++
		/*
		if (StringUtils.isEmpty(user.getFaceSubjectId())) {
			//register
			FacePlusPlusUser faceUser = new FacePlusPlusUser();
			faceUser.setSubject_type(FacePlusPlusUser.SUBJECT_TYPE_EMPLOYEE);
			faceUser.setName(user.getUserName());
			faceUser.setPhoto_ids(photoIds);
			Map<String, Object> userMap = facePlusPlusService.buildSubject(faceUser);
			Integer faceSubjectId = (Integer) ((Map<String, Object>) userMap.get("data")).get("id");
			if (faceSubjectId == null) {
				throw new IllegalArgumentException("register face++ user error ! face++:" + userMap);
			}
			//
			user.setFaceSubjectId(faceSubjectId);
		} else {
			// update
//			throw new RuntimeException("user already registered face++! ");
//			if (!clearOldPhoto) {
//				// 保留原来的 照片
//				Map<String, Object> userMap = faceService.buildGetSubjectById(user.getFaceSubjectId());
//				List<Map<String, Object>> oldPhotos = (List<Map<String, Object>>) (((Map<String, Object>) userMap.get("data")).get("photos"));
//				for (Map<String, Object> lodPhoto : oldPhotos) {
//					photoIds.add((Integer) lodPhoto.get("id"));
//				}
//			}

			FacePlusPlusUser faceUser = new FacePlusPlusUser();
			faceUser.setId(user.getFaceSubjectId());
			faceUser.setName(user.getUserName());
			faceUser.setPhoto_ids(photoIds);
			facePlusPlusService.buildUpdateSubject(faceUser);
		}

		userManager.updateUser(user, userId);
		*/

		return user;
	}
}
