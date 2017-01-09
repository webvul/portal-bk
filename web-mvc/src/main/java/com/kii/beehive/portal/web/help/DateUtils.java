package com.kii.beehive.portal.web.help;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by user on 16/12/28.
 */
public class DateUtils {

	static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static Date formatToDate(String source) throws ParseException {
		return formatter.parse(source);
	}

}
