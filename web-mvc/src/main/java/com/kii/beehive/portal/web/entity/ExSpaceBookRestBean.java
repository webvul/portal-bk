package com.kii.beehive.portal.web.entity;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kii.beehive.business.service.ExSpaceBookService;
import com.kii.beehive.portal.jdbc.entity.ExSpaceBook;

public class ExSpaceBookRestBean {



	private String app_code;
	private String campus_code;
	private String biz_id;
	private String biz_type;

	private List<ExSpaceBookRestBean> userList;

	private String user_id;
	private String password;
	private String space_code;
	private String begin_date;
	private String end_date;

	private String sign;
	private String token;

	public String getApp_code() {
		return app_code;
	}

	public void setApp_code(String app_code) {
		this.app_code = app_code;
	}

	public String getCampus_code() {
		return campus_code;
	}

	public void setCampus_code(String campus_code) {
		this.campus_code = campus_code;
	}

	public String getBiz_id() {
		return biz_id;
	}

	public void setBiz_id(String biz_id) {
		this.biz_id = biz_id;
	}

	public String getBiz_type() {
		return biz_type;
	}

	public void setBiz_type(String biz_type) {
		this.biz_type = biz_type;
	}

	public List<ExSpaceBookRestBean> getUserList() {
		return userList;
	}

	public void setUserList(List<ExSpaceBookRestBean> userList) {
		this.userList = userList;
	}


	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSpace_code() {
		return space_code;
	}

	public void setSpace_code(String space_code) {
		this.space_code = space_code;
	}

	public String getBegin_date() {
		return begin_date;
	}

	public void setBegin_date(String begin_date) {
		this.begin_date = begin_date;
	}

	public String getEnd_date() {
		return end_date;
	}

	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@JsonIgnore
	public List<ExSpaceBook> convert2ExSpaceBook() {
		List<ExSpaceBook> result = new ArrayList<>();

		userList.forEach( bean -> {
			ExSpaceBook spaceBook = new ExSpaceBook();
			spaceBook.setAppCode(app_code);
			spaceBook.setCampusCode(campus_code);
			spaceBook.setBizId(biz_id);
			spaceBook.setBizType(biz_type);
			spaceBook.setUserId(bean.user_id);
			spaceBook.setPassword(bean.password);
			spaceBook.setSpaceCode(bean.space_code);
			try {
				if(bean.begin_date != null) spaceBook.setBeginDate(DateUtils.parseDate(bean.begin_date,"yyyy-MM-dd HH:mm:ss"));
			} catch (ParseException e) {
				throw new IllegalArgumentException("begin_date valid input");
			}
			try {
				if(bean.end_date != null) spaceBook.setEndDate(DateUtils.parseDate(bean.end_date,"yyyy-MM-dd HH:mm:ss"));
			} catch (ParseException e) {
				throw new IllegalArgumentException("end_date valid input");
			}

			result.add(spaceBook);
		});

		return result;
	}
	@JsonIgnore
	public void verifyInput() {
		if (StringUtils.isBlank(app_code)) {
			throw new IllegalArgumentException("app_code can not null");
		}
		if ( ! ExSpaceBookService.SIT_BOOKING_APP_CODE.equals(app_code)) {
			throw new IllegalArgumentException("app_code valid!");
		}
		if ( ! ExSpaceBookService.SIT_BOOKING_APP_CODE.equals(app_code)) {
			throw new IllegalArgumentException("app_code valid!");
		}
		if (StringUtils.isBlank(campus_code)) {
			throw new IllegalArgumentException("campus_code can not null");
		}
		if (userList == null) {
			throw new IllegalArgumentException("userList can not null");
		}
		userList.forEach( bean -> {
			if (StringUtils.isBlank(bean.user_id)) {
				throw new IllegalArgumentException("user_id can not null");
			}
			if (StringUtils.isBlank(bean.password)) {
				throw new IllegalArgumentException("password can not null");
			}
//			if (bean.password.length() != 6) {
//				throw new IllegalArgumentException("password valid input");
//			}
			if (StringUtils.isBlank(bean.space_code)) {
				throw new IllegalArgumentException("space_code can not null");
			}
			if (bean.begin_date == null) {
				throw new IllegalArgumentException("begin_date can not null");
			}
			if (bean.end_date == null) {
				throw new IllegalArgumentException("end_date can not null");
			}

		});
	}
	@JsonIgnore
	public void verifyDelInput() {
		if (StringUtils.isBlank(app_code)) {
			throw new IllegalArgumentException("app_code can not null");
		}
		if (StringUtils.isBlank(campus_code)) {
			throw new IllegalArgumentException("campus_code can not null");
		}
		if (userList == null) {
			throw new IllegalArgumentException("userList can not null");
		}
		userList.forEach( bean -> {
			if (StringUtils.isBlank(bean.user_id)) {
				throw new IllegalArgumentException("user_id can not null");
			}
		});
	}

	private String old_password;
	private String new_password;

	public String getOld_password() {
		return old_password;
	}

	public void setOld_password(String old_password) {
		this.old_password = old_password;
	}

	public String getNew_password() {
		return new_password;
	}

	public void setNew_password(String new_password) {
		this.new_password = new_password;
	}

	@JsonIgnore
	public ExSpaceBook convert2ExSpaceBookForUpdatePwd() {
		ExSpaceBook spaceBook = new ExSpaceBook();
		spaceBook.setAppCode(app_code);
		spaceBook.setCampusCode(campus_code);
		spaceBook.setBizId(biz_id);
		spaceBook.setBizType(biz_type);
		spaceBook.setUserId(user_id);
		spaceBook.setPassword(old_password);
		return spaceBook;
	}
	@JsonIgnore
	public void verifyUpdatePwdInput() {
		if (StringUtils.isBlank(app_code)) {
			throw new IllegalArgumentException("app_code can not null");
		}
		if ( ! ExSpaceBookService.SIT_BOOKING_APP_CODE.equals(app_code)) {
			throw new IllegalArgumentException("app_code valid!");
		}
		if (StringUtils.isBlank(campus_code)) {
			throw new IllegalArgumentException("campus_code can not null");
		}
		if (StringUtils.isBlank(user_id)) {
			throw new IllegalArgumentException("user_id can not null");
		}
//		if (StringUtils.isBlank(old_password)) {
//			throw new IllegalArgumentException("old_password can not null");
//		}
		if (StringUtils.isBlank(new_password)) {
			throw new IllegalArgumentException("user_id can not null");
		}

	}


}
