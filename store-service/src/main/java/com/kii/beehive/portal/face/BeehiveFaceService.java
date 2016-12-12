package com.kii.beehive.portal.face;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import com.google.common.io.Files;
import com.kii.beehive.portal.exception.EntryNotFoundException;
import com.kii.beehive.portal.face.entitys.FaceImage;
import com.kii.beehive.portal.face.faceplusplus.FacePlusPlusService;
import com.kii.beehive.portal.face.faceplusplus.entitys.FacePlusPlusUser;
import com.kii.beehive.portal.face.faceyitu.YituFaceService;
import com.kii.beehive.portal.face.faceyitu.entitys.YituFaceImage;
import com.kii.beehive.portal.jdbc.entity.BeehiveJdbcUser;
import com.kii.beehive.portal.manager.BeehiveUserManager;

@Component
public class BeehiveFaceService implements ApplicationContextAware{

	@Autowired
	private BeehiveUserManager userManager;

	@Autowired
	private YituFaceService yituFaceService;
	@Autowired
	private FacePlusPlusService facePlusPlusService;

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

	public File getPhotoTempDir() {
		return photoTempDir;
	}


	private Map<String, FaceServiceInf> faceServiceMap;
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
		faceServiceMap = applicationContext.getBeansOfType(FaceServiceInf.class);
	}

	public BeehiveJdbcUser updateUserWithFace(String userId, File photoFile) {


		BeehiveJdbcUser user = userManager.getUserByUserID(userId);
		if (user == null) {
			throw EntryNotFoundException.userIDNotFound(userId);
		}
		//face++
		Map<String, Object> photoMap = facePlusPlusService.buildUploadPhoto(photoFile);
		List<Integer> photoIds = new ArrayList<>();
		Integer photoId = (Integer) ((Map<String, Object>) photoMap.get("data")).get("id");
		if (photoId == null) {
			throw new RuntimeException("upload face++ photo error ! " + photoMap.get("desc"));
		}
		photoIds.add(photoId);
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
		FaceImage faceImage = yituFaceService.doUploadImage(yituFaceImage);
		if(faceImage == null) {
			throw new RuntimeException("upload yitu photo error ! ");
		}
		//store image
		try {
			Files.copy(photoFile, new File(facePhotoDir + user.getUserID() + ".jpg"));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("store photo error ! ");
		}
		//yitu
		if (! StringUtils.isEmpty(user.getYituFaceImageId())) {
			yituFaceService.doDeleteImage(user.getYituFaceImageId());
		}
		user.setYituFaceImageId(String.valueOf(faceImage.getFace_image_id()));
		//face++
		if (StringUtils.isEmpty(user.getFaceSubjectId())) {
			//register
			FacePlusPlusUser faceUser = new FacePlusPlusUser();
			faceUser.setSubject_type(FacePlusPlusUser.SUBJECT_TYPE_EMPLOYEE);
			faceUser.setName(user.getUserName());
			faceUser.setPhoto_ids(photoIds);
			Map<String, Object> userMap = facePlusPlusService.buildSubject(faceUser);
			Integer faceSubjectId = (Integer) ((Map<String, Object>) userMap.get("data")).get("id");
			if (faceSubjectId == null) {
				throw new RuntimeException("register face++ user error ! ");
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
		return user;
	}
}
