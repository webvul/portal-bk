package com.kii.extension.sdk.commons;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.springframework.util.FileCopyUtils;

public class HttpUtils {

	public static String getResponseBody(HttpResponse response){

		try{
			HttpEntity entity=response.getEntity();
			if(entity!=null) {

				String result = new String(FileCopyUtils.copyToByteArray(response.getEntity().getContent()), "UTF-8");

				return result;
			}else{
				return "";
			}

		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}
}
