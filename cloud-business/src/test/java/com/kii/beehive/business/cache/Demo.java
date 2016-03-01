package com.kii.beehive.business.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class Demo {

	private Logger log= LoggerFactory.getLogger(Demo.class);

	private Map<Integer,String> map=new HashMap<>();


//	@Cacheable("demo_cache")
//	public String getFoo(int idx){
//
//		return "foo"+System.currentTimeMillis()+""+idx;
//	}



	@CacheEvict(cacheNames ="demo_cache",key="'all'")
	@CachePut(cacheNames="demo_cache",key="#idx")
	public String  addFoo(int idx,String value){

		map.put(idx,value);

		return value;
	}

	@Cacheable(cacheNames = "demo_cache",key="'all'")
	public List<String> getValueList(){

		return new ArrayList<>(map.values());

	}

	@Cacheable(cacheNames = "demo_cache",key="#idx")
	public String getValue(int idx){
		return map.get(idx);
	}

	@CacheEvict(cacheNames ="demo_cache",key="'zero'")
	public void setZero(String value){
		map.put(0,value);


	}

	@Cacheable(cacheNames="demo_cache",key="'zero'")
	public String getZero(){
		return map.get(0);
	}
}
