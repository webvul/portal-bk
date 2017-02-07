package com.kii.beehive.portal.sysmonitor;

import java.util.Date;

public class SysMonitorMsg {
	
	public enum FromType{
		RuleEngine,DB,KiiApp;
	}
	
	
	private Date fireDate=new Date();
	
	private FromType from;
	
	private String errMessage;
	
	private String errorType;
	
	public Date getFireDate() {
		return fireDate;
	}
	
	public void setFireDate(Date fireDate) {
		this.fireDate = fireDate;
	}
	
	public FromType getFrom() {
		return from;
	}
	
	public void setFrom(FromType from) {
		this.from = from;
	}
	
	public String getErrMessage() {
		return errMessage;
	}
	
	public void setErrMessage(String errMessage) {
		this.errMessage = errMessage;
	}
	
	public String getErrorType() {
		return errorType;
	}
	
	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}
}
