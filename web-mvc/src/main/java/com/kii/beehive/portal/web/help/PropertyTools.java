package com.kii.beehive.portal.web.help;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.google.common.base.Charsets;

import com.kii.beehive.portal.common.utils.SubStrUtils;

@Component
public class PropertyTools {


	@Autowired
	private ResourceLoader  loader;


	private String propFilePath="classpath:com/kii/beehive/portal/i18n/";

	private Map<Locale,PropertyEntry>  propertyMap=new HashMap<>();


	public PropertyEntry getPropertyEntry(String name, Locale locale){


		return  propertyMap.computeIfAbsent(locale,(k)->{
			try {
				return loadPropertyFile(name,locale);
			} catch (IOException e) {
				throw new IllegalArgumentException(e);
			}
		});


	}

	private  PropertyEntry loadPropertyFile(String name, Locale locale)throws IOException{


		String localeSeq=locale.getDisplayName();

		String fullFileName=propFilePath+name+"."+localeSeq+".properties";

		try(InputStream stream=loader.getResource(fullFileName).getInputStream();
			Reader reader=new InputStreamReader(stream, Charsets.UTF_8)){

			return new PropertyEntry(reader);
		}
	}


	public  static class PropertyEntry{


		private final Map<String,String> valueMap=new HashMap<>();

		public PropertyEntry(Reader reader) throws IOException {
			BufferedReader  lineReader=new BufferedReader(reader);

			String line=lineReader.readLine();

			while(line!=null){

				String key= SubStrUtils.getBeforeSep(line,'=');
				String value=SubStrUtils.getAfterSep(line,'=');

				valueMap.put(key,value);
			}
		}

		public String getPropertyValue(String propName){
			return valueMap.get(propName);
		}

	}
}
