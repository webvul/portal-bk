package com.kii.extension.tools;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonToFlat {


	private static ObjectMapper mapper;

	static {

		mapper=new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		mapper.configure(SerializationFeature.INDENT_OUTPUT, false);
	}

	public static String flatJson(String jsonStr) throws IOException {

		Map<String,Object>  map=mapper.readValue(jsonStr,Map.class);

		Map<String,Object> result=new HashMap<>();

		fillMap(result,map,null);

		return mapper.writeValueAsString(result);

	}

	private static void fillMap(Map<String,Object> result,Object source,String prefix){

		if (Map.class.isAssignableFrom(source.getClass())) {

			Map subMap=(Map)source;

			subMap.forEach((k, v) -> {
				fillMap(result,  v, getNextPrefix(prefix,k));
			});

		}else if(Collection.class.isAssignableFrom(source.getClass())){

			int idx = 0;
			for (Object o : ((Collection) source)) {
				fillMap(result, o,getNextPrefix(prefix,idx));
				idx++;
			}
		}else{

			result.put(prefix,source);
		}

		return;
	}


	private static String getNextPrefix(String prefix,Object curr){
		if(prefix==null){

			return String.valueOf(curr);
		}else{
			return prefix+"."+curr;
		}
	}

}
