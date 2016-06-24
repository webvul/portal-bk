package com.kii.beehive.portal.extend.entitys;

import java.util.List;

/**
 * Created by user on 16/6/21.
 */
public class FaceUser {
	//用户类型 {0:员工, 1:访客, 2: VIP访客}
	public static int SUBJECT_TYPE_EMPLOYEE = 0;
	public static int SUBJECT_TYPE_VISITOR = 1;
	public static int SUBJECT_TYPE_VISITOR_VIP = 2;

	private Integer id;
	private String name;
	private int subject_type;//用户类型 {0:员工, 1:访客, 2: VIP访客}
	private List<Integer> photo_ids;


	public int getSubject_type() {
		return subject_type;
	}

	public void setSubject_type(int subject_type) {
		this.subject_type = subject_type;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Integer> getPhoto_ids() {
		return photo_ids;
	}

	public void setPhoto_ids(List<Integer> photo_ids) {
		this.photo_ids = photo_ids;
	}
}
