package com.kii.beehive.business.helper;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kii.beehive.portal.jdbc.entity.ExSpaceBook;

/**
 * Created by user on 17/1/3.
 */
public class IBCommonUtil {

	public final static String SIGN_KEY = "alibaba123456789";
	public final static ObjectMapper objectMapper = new ObjectMapper();
	static {
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	}

	public static void main(String[] args) throws Exception {


		Map<String,String> map = new HashMap<>();
		map.put("biz_id", "biz_id_value");
		map.put("app_code", "app_code_value");
		map.put("campus_code", "campus_code_value");
		map.put("biz_type", null);
		map.put("userList", "userList_value");

		ExSpaceBook book = new ExSpaceBook();
		book.setSpaceCode("sdfsdf");

		System.out.println(objectMapper.writeValueAsString(book));
		System.out.println(objectMapper.writeValueAsString(map));

//		System.out.println(IBCommonUtil.signMapKey(map));

	}

	public static String writeValueAsString(Object o)throws JsonProcessingException {
		return objectMapper.writeValueAsString(o);
	}

	public static String signMapKey(Map<String,String> map) {
		List<Map.Entry<String, String>> entryList = new ArrayList<>(map.entrySet());

		Collections.sort(entryList, new Comparator<Map.Entry<String,String>>() {
			@Override
			public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
				return (o1.getKey()).compareTo(o2.getKey());
			}
		});
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : entryList) {
			String value = entry.getValue();
			if (value == null) {
				continue;
			}
			if (sb.length() > 0) {
				sb.append("&");
			}
			sb.append(entry.getKey()).append("=").append(value);
		}
		return IBCommonUtil.getMD5Code(sb.append(SIGN_KEY).toString());
	}


	public static String getMD5Code(String strObj){

		String result = "";
		if (strObj != null) {
			try {
				// 指定加密的方式为MD5
				MessageDigest md = MessageDigest.getInstance("MD5");
//                byte2hexString(md.digest(strObj.getBytes().getBytes("UTF-8"));
				return byte2hexString(md.digest(strObj.getBytes("UTF-8")));
//                result = byteToString(md.digest(strObj.getBytes()));
			} catch (Exception e) {

			}
		}
		return result.toLowerCase();
	}


	private static String byte2hexString(byte[] b) {
		StringBuilder sb = new StringBuilder("");
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1) {
				sb.append("0").append(stmp);
			} else {
				sb.append(stmp);
			}
		}
		return sb.toString();
	}


}
