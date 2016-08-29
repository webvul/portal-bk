package com.kii.beehive.obix.web.entity;

import com.kii.beehive.obix.store.PointDataType;

public enum ObixType {

	OBJ,BOOL,INT,REAL,STR,ENUM,ABSTIME,RELTIME,DATE,TIME,URI,LIST,REF,ERR,OP,FEED;


	public static ObixType getInstance(PointDataType type){

		//	Int,Float,Enum,Boolean,String,Datetime;

		switch(type){
			case Int:return INT;
			case Float:return REAL;
			case Enum:return ENUM;
			case Boolean:return BOOL;
			case String:return STR;
			case Datetime:return ABSTIME;
			default:return OBJ;
		}
	}

}
