package com.kii.beehive.portal.web.entity;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import com.kii.beehive.portal.jdbc.entity.BeehiveJdbcUser;
import com.kii.beehive.portal.web.exception.ErrorCode;
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
	private static Pattern mailPattern=Pattern.compile("^[\\w\\.]+@[\\w\\.]+$");

	@JsonIgnore
	public void verifyInput(){
		if(StringUtils.isBlank(beehiveUser.getUserName())&&StringUtils.isBlank(beehiveUser.getMail())&&StringUtils.isBlank(beehiveUser.getPhone())){
			throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING, "field","userName or mail or phone ");
		}

		if(StringUtils.isNoneBlank(beehiveUser.getUserName())&&!StringUtils.isAsciiPrintable(beehiveUser.getUserName())){
			throw new  PortalException(ErrorCode.INVALID_INPUT, "field","userName","data",beehiveUser.getUserName());
		}

		if(StringUtils.isBlank(beehiveUser.getPhone())){
			beehiveUser.setPhone(null);
		}else if(!numPattern.matcher(beehiveUser.getPhone()).find()){
			throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING,"field","phone");
		}


		if(StringUtils.isBlank(beehiveUser.getMail())){
			beehiveUser.setMail(null);
		}else if(!mailPattern.matcher(beehiveUser.getMail()).find()){
			throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING,"field","mail");
		}


		if (StringUtils.isBlank(beehiveUser.getUserName())) {
			if (!StringUtils.isBlank(beehiveUser.getMail())) {
				beehiveUser.setUserName(beehiveUser.getMail());
			} else if (!StringUtils.isBlank(beehiveUser.getPhone())) {
				beehiveUser.setUserName(beehiveUser.getPhone());
			}
		}

	}
}
