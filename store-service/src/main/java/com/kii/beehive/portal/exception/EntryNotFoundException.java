package com.kii.beehive.portal.exception;


import java.util.Collection;
import java.util.List;

import org.apache.http.HttpStatus;

public class EntryNotFoundException extends BusinessException{

	public static EntryNotFoundException thingNotFound(long thingID){

		EntryNotFoundException excep=new EntryNotFoundException(String.valueOf(thingID),"beehive thing");

		return excep;
	}

	public static EntryNotFoundException thingNotFound(Collection<?> thingIDs){

		EntryNotFoundException excep=new EntryNotFoundException(String.valueOf(thingIDs),"beehive thing");

		return excep;
	}
	public static EntryNotFoundException thingNotFound(String thingID){

		EntryNotFoundException excep=new EntryNotFoundException(String.valueOf(thingID),"kiicloud thing");

		return excep;
	}

	public static EntryNotFoundException existsNullTag(Collection<?> tagIDs){

		EntryNotFoundException excep=new EntryNotFoundException(String.valueOf(tagIDs),"beehive tag");

		excep.setErrorCode("TAG_NOT_EXIST");
		return excep;
	}


	public static EntryNotFoundException tagNameNotFound(String  tagName){

		EntryNotFoundException excep=new EntryNotFoundException(String.valueOf(tagName),"beehive tag");

		excep.setErrorCode("TAGNAME_NOT_EXIST");
		return excep;
	}

	public static EntryNotFoundException appNotFound(String  thingID){

		EntryNotFoundException excep=new EntryNotFoundException(String.valueOf(thingID),"kiiApp");

		return excep;
	}

	public static EntryNotFoundException userNotFound(List<String> userName){
		EntryNotFoundException excep=new EntryNotFoundException(String.valueOf(userName),"beehiveUser");

		return excep;

	}

	public static  EntryNotFoundException userIDNotFound(Object userName){
		EntryNotFoundException excep=new EntryNotFoundException(String.valueOf(userName),"beehiveUser");

		return excep;

	}
	public static EntryNotFoundException userGroupNotFound(List<Long> userName){
		EntryNotFoundException excep=new EntryNotFoundException(String.valueOf(userName),"userGroup");

		return excep;

	}


	public EntryNotFoundException(String objectID,String objectType){

		super.setErrorCode("BEEHIVE_OBJECT_NOT_EXIST");

		super.addParam("objectID",objectID);
		super.addParam("type",objectType);

		super.setStatusCode(HttpStatus.SC_NOT_FOUND);

	}

}
