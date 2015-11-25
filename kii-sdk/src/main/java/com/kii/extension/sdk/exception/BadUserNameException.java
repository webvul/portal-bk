package com.kii.extension.sdk.exception;

import java.util.Map;
import java.util.Set;

public class BadUserNameException extends KiiCloudException {

	/*
	< "[\r][\n]"
2015-11-24 18:07:33,504 DEBUG nio.conn.Wire.wire(54) - http-outgoing-0 << "{[\n]"
2015-11-24 18:07:33,504 DEBUG nio.conn.Wire.wire(54) - http-outgoing-0 << "  "errorCode" : "INVALID_INPUT_DATA",[\n]"
2015-11-24 18:07:33,504 DEBUG nio.conn.Wire.wire(54) - http-outgoing-0 << "  "message" : "There are validation errors: loginName - Must be made of letters, numbers and/or '_'. Length must be between 3 and 64 chars. ",[\n]"
2015-11-24 18:07:33,505 DEBUG nio.conn.Wire.wire(54) - http-outgoing-0 << "  "invalidFields" : {[\n]"
2015-11-24 18:07:33,505 DEBUG nio.conn.Wire.wire(54) - http-outgoing-0 << "    "loginName" : "Must be made of letters, numbers and/or '_'. Length must be between 3 and 64 chars"[\n]"
2015-11-24 18:07:33,505 DEBUG nio.conn.Wire.wire(54) - http-outgoing-0 << "  }[\n]"
2015-11-24 18:07:33,505 DEBUG nio.conn.Wire.wire(68) - http-outgoing-0 << "}"
2015-11-24 18:07:33,506 DEBUG nio.conn.ManagedNHttpClientConnectionImpl.onRes
	 */

	private Map<String,String> invalidFields;

	public Map<String, String> getInvalidFields() {
		return invalidFields;
	}

	public void setInvalidFields(Map<String, String> invalidFields) {
		this.invalidFields = invalidFields;
	}


	@Override
	public int getStatusCode(){
		return 400;
	}

}
