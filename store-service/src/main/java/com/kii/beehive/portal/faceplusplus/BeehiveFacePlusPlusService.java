package com.kii.beehive.portal.faceplusplus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import com.kii.beehive.portal.exception.EntryNotFoundException;
import com.kii.beehive.portal.faceplusplus.entitys.FaceUser;
import com.kii.beehive.portal.jdbc.entity.BeehiveJdbcUser;
import com.kii.beehive.portal.manager.BeehiveUserManager;

@Component
public class BeehiveFacePlusPlusService {


	@Autowired
	private BeehiveUserManager userManager;

	@Autowired
	private FacePlusPlusService facePlusPlusService;


	public BeehiveJdbcUser updateUserWithFace(String userId, Boolean clearOldPhoto, List<File> photoFiles) {


		BeehiveJdbcUser user = userManager.getUserByUserID(userId);
		if (user == null) {
			throw EntryNotFoundException.userIDNotFound(userId);
		}

		List<Integer> photoIds = new ArrayList<>();
		List<Map<String, Object>> photoList = facePlusPlusService.buildUploadPhotos(photoFiles);

		for (Map<String, Object> photoMap : photoList) {
			Integer photoId = (Integer) ((Map<String, Object>) photoMap.get("data")).get("id");
			if (photoId == null) {
				throw new RuntimeException("upload face++ photo error ! " + photoMap.get("desc"));
			}
			photoIds.add(photoId);
		}


		if (StringUtils.isEmpty(user.getFaceSubjectId())) { // register
			FaceUser faceUser = new FaceUser();
			faceUser.setSubject_type(FaceUser.SUBJECT_TYPE_EMPLOYEE);
			faceUser.setName(user.getUserName());
			faceUser.setPhoto_ids(photoIds);
			Map<String, Object> userMap = facePlusPlusService.buildSubject(faceUser);
			Integer faceSubjectId = (Integer) ((Map<String, Object>) userMap.get("data")).get("id");
			if (faceSubjectId == null) {
				throw new RuntimeException("register face++ user error ! ");
			}
			//
			user.setFaceSubjectId(faceSubjectId);
			userManager.updateUser(user, userId);
		} else {// update
//			throw new RuntimeException("user already registered face++! ");
			if (!clearOldPhoto) {
				// 保留原来的 照片
				Map<String, Object> userMap = facePlusPlusService.buildGetSubjectById(user.getFaceSubjectId());
				List<Map<String, Object>> oldPhotos = (List<Map<String, Object>>) (((Map<String, Object>) userMap.get("data")).get("photos"));
				for (Map<String, Object> lodPhoto : oldPhotos) {
					photoIds.add((Integer) lodPhoto.get("id"));
				}
			}

			FaceUser faceUser = new FaceUser();
			faceUser.setId(user.getFaceSubjectId());
			faceUser.setName(user.getUserName());
			faceUser.setPhoto_ids(photoIds);
			facePlusPlusService.buildUpdateSubject(faceUser);
		}

		return user;
	}
}
