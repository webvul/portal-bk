package com.kii.extension.tools;

import java.util.Calendar;

public class CronGeneral {

	public static  String getCurrentCron(Calendar  cal){

		StringBuilder sb=new StringBuilder(" 0 ");

		sb.append(cal.get(Calendar.MINUTE)).append(" ");
		sb.append(cal.get(Calendar.HOUR_OF_DAY)).append(" ");
		sb.append(cal.get(Calendar.DAY_OF_MONTH)).append(" ");
		sb.append(cal.get(Calendar.MONTH)+1).append(" ");
		sb.append(" ? ");

		return sb.toString();

	}
}
