package com.kii.beehive.portal.jdbc.annotation;


import java.sql.Types;

import static org.springframework.jdbc.core.namedparam.SqlParameterSource.TYPE_UNKNOWN;

public enum JdbcFieldType {

	Auto,Json,EnumInt,EnumStr;

	public int getSqlType() {

		switch (this) {
			case Json:
				return Types.LONGVARCHAR;
			case EnumInt:
				return Types.INTEGER;
			case EnumStr:
				return Types.VARCHAR;
			default:
				return TYPE_UNKNOWN;
		}
	}
}
