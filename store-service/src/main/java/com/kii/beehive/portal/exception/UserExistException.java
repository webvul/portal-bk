package com.kii.beehive.portal.exception;

import org.apache.http.HttpStatus;

import com.kii.beehive.portal.jdbc.entity.BeehiveJdbcUser;


public class UserExistException extends BusinessException {


	public UserExistException(BeehiveJdbcUser user,BeehiveJdbcUser existUser){

		super.setErrorCode("USER_EXIST");

		super.setStatusCode(HttpStatus.SC_BAD_REQUEST);

		if(user.getUserName().equals(existUser.getUserName())){

			super.addParam("field","userName");
			super.addParam("value",user.getUserName());
		}


		if(user.getMail().equals(existUser.getMail())){

			super.addParam("field","mail");
			super.addParam("value",user.getMail());
		}


		if(user.getPhone().equals(existUser.getPhone())){

			super.addParam("field","phone");
			super.addParam("value",user.getPhone());
		}

	}
}
