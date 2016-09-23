package com.kii.beehive.obix.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;

import com.kii.beehive.obix.web.entity.ObixContain;

@Component
public class LabbyService {

	@Autowired
	private ResourceLoader loader;

	@Autowired
	private ObjectMapper mapper;

	public ObixContain getLabby(){
		try {
			String json = StreamUtils.copyToString(loader.getResource("classpath:com/kii/beehive/obix/define/Labby.json").getInputStream(), Charsets.UTF_8);


			ObixContain schema = mapper.readValue(json, ObixContain.class);

			return schema;
		}catch(IOException e){
			throw new IllegalArgumentException(e);
		}

	}

	public String getHaystackTags(){

		try {
			return  StreamUtils.copyToString(loader.getResource("classpath:com/kii/beehive/obix/define/tags.txt").getInputStream(), Charsets.UTF_8);

		}catch(IOException e){
			throw new IllegalArgumentException(e);
		}
	}

}
