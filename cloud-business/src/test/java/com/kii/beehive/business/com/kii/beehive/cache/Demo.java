package com.kii.beehive.business.com.kii.beehive.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class Demo {

	private Logger log= LoggerFactory.getLogger(Demo.class);


	@Cacheable("demo_cache")
	public String getFoo(int idx){

		return "foo"+System.currentTimeMillis()+""+idx;
	}
}
