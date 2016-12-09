package com.kii.beehive.portal.jdbc.annotation;


import static org.springframework.jdbc.core.namedparam.SqlParameterSource.TYPE_UNKNOWN;

import java.sql.Types;

public enum JdbcFieldType {

	Auto,Json,EnumInt,EnumStr,AdditionStr,AdditionInt;

	public int getSqlType() {

		switch (this) {
			case Json:
				return Types.LONGVARCHAR;
			case EnumInt:
				return Types.INTEGER;
			case EnumStr:
			case AdditionInt:
			case AdditionStr:
				return Types.VARCHAR;
			
			default:
				return TYPE_UNKNOWN;
		}
	}
}
