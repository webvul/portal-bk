package com.kii.extension.tools;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonToFlat {


	private static ObjectMapper mapper=new ObjectMapper();

	static {

		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		mapper.configure(SerializationFeature.INDENT_OUTPUT, false);
	}

	public static String flatJson(String jsonStr) throws IOException {

		Map<String,Object>  map=mapper.readValue(jsonStr,Map.class);

		Map<String,Object> result=new HashMap<>();
		return mapper.writeValueAsString(result);

	}

	private static void fillMap(Map<String,Object> result,Object source,String prefix){



		result.forEach((k,v)->{

			if(v instanceof  Map){
				fillMap(result,(Map)v,prefix+"."+k);
			}else  if(v instanceof Collection){

				int idx=0;
				for(Object o:((Collection)v)){
					fillMap(result,o,prefix+"."+idx);
					idx++;
				}

			}else {
				result.put(prefix + "." + k, v);
			}
		});

		return;
	}


}
