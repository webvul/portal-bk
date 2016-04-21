package com.kii.extension.tools;

import java.util.HashMap;
import java.util.Map;

public class Service {

	private Map<String,String> store=new HashMap<>();


	private final String name;
	private final int num;
	public Service(String name,int num){
		this.name=name;
		this.num=num;
	}

	public String getName(){
		return name;
	}

	public long[] getHashArray(){

		long[] array=new long[num];

		for(int i=0;i<num;i++){
			array[i]=(name+"_subfix_"+i+"_hash").hashCode();
		}

		return array;
	}

	public void addData(String key,String data){

		store.put(key,data);
	}

	public String getData(String key){
		return store.get(key);
	}

	public void removeData(String  key){
		store.remove(key);
	}



}
