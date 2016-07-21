package com.kii.beehive.portal.exception;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;

import com.kii.beehive.portal.jdbc.entity.BeehiveJdbcUser;


public class UserExistException extends BusinessException {


	public UserExistException(BeehiveJdbcUser user,BeehiveJdbcUser existUser){

		super.setErrorCode("USER_EXIST");

		super.setStatusCode(HttpStatus.SC_CONFLICT);

		if(StringUtils.isNoneBlank(user.getUserName()) && StringUtils.equals(user.getUserName(), existUser.getUserName())){

			super.addParam("field","userName");
			super.addParam("value",user.getUserName());
		}

		if(StringUtils.isNoneBlank(user.getMail())  && StringUtils.equals(user.getMail(), existUser.getMail())){

			super.addParam("field","mail");
			super.addParam("value",user.getMail());
		}

		if(StringUtils.isNoneBlank(user.getPhone())  && StringUtils.equals(user.getPhone(), existUser.getPhone())){

			super.addParam("field","phone");
			super.addParam("value",user.getPhone());
		}

	}
}
