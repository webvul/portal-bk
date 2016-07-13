package com.kii.beehive.portal.web.entity;

import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import com.kii.beehive.portal.jdbc.entity.BeehiveJdbcUser;
import com.kii.beehive.portal.web.constant.ErrorCode;
import com.kii.beehive.portal.web.exception.PortalException;

public class UserRestBean {


	private BeehiveJdbcUser beehiveUser;


	private String password;

	private String teamName;

	public UserRestBean(){

	}


	public UserRestBean(BeehiveJdbcUser user){
		this.beehiveUser = user;
	}


	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}



	@JsonUnwrapped
	public BeehiveJdbcUser getBeehiveUser() {
		return beehiveUser;
	}

	public void setBeehiveUser(BeehiveJdbcUser user) {
		this.beehiveUser = user.cloneView();
	}

	private static Pattern numPattern=Pattern.compile("^1[\\d]{10}$");

	@JsonIgnore
	public void verifyInput(){
		if(StringUtils.isEmpty(beehiveUser.getUserName())&&StringUtils.isEmpty(beehiveUser.getMail())&&StringUtils.isEmpty(beehiveUser.getPhone())){
			PortalException excep= new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING, HttpStatus.BAD_REQUEST);
			excep.addParam("field","userName or mail or phone ");
			throw excep;		}

		if(!StringUtils.isEmpty(beehiveUser.getPhone())&& !numPattern.matcher(beehiveUser.getPhone()).find()){

			PortalException excep= new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING, HttpStatus.BAD_REQUEST);
			excep.addParam("field","phone ");
			throw excep;
		}

	}
}
