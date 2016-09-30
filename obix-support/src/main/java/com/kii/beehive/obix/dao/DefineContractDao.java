package com.kii.beehive.obix.dao;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;

import com.kii.beehive.obix.web.entity.ObixContain;

@Component
public class DefineContractDao {




	@Autowired
	private ResourceLoader loader;


	@Autowired
	private ObjectMapper mapper;


	public ObixContain getDefineContract(String name){

		try {
			String json = StreamUtils.copyToString(loader.getResource("classpath:com/kii/beehive/obix/define/" + name + ".json").getInputStream(), Charsets.UTF_8);


			ObixContain schema = mapper.readValue(json, ObixContain.class);

			return schema;
		}catch(IOException e){
			throw new IllegalArgumentException(e);
		}
	}

}
