package com.kii.beehive.portal.common.utils;

import java.util.List;

import org.springframework.util.CollectionUtils;

public class CollectUtils {


	public static List<String> createList(String... array){

		return CollectionUtils.arrayToList(array);
	}

	public static <T> T getFirst(List<T> list) {

		if(list == null || list.isEmpty()) {
			return null;
		}

		return (T)list.get(0);
	}
}
