package com.kii.extension.ruleengine.sdk.exception;

public class UnauthorizedAccessException extends KiiCloudException {

	private static final long serialVersionUID = -6136853872245385114L;

	private String authenticatedAppID;

	private String authenticatedPrincipalID;

	public UnauthorizedAccessException() {
		super();
	}

	@Override
	public int getStatusCode(){
		return 401;
	}
	
	@Override
	public String getErrorMessage() {
		return "unauthorized access";
	}

	public String getAuthenticatedAppID() {
		return authenticatedAppID;
	}

	public void setAuthenticatedAppID(String authenticatedAppID) {
		this.authenticatedAppID = authenticatedAppID;
	}

	public String getAuthenticatedPrincipalID() {
		return authenticatedPrincipalID;
	}

	public void setAuthenticatedPrincipalID(String authenticatedPrincipalID) {
		this.authenticatedPrincipalID = authenticatedPrincipalID;
	}
}
