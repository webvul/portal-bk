package com.kii.extension.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataStore {


	private Map<String,String> dataMap=new HashMap<>();

	private Map<String,String> serviceMap=new HashMap<>();


	private ServiceList  serviceList=new ServiceList();


	public void init(int serviceNum,int repNum){

		List<Service> list=new ArrayList<>();

		for(int i=0;i<serviceNum;i++){
			list.add(new Service("serviceName:"+i,repNum));
		}

		serviceList.initServiceList(list);
	}

	public void addData(String key,String data){

		dataMap.put(key,data);

		long hash=key.hashCode();

		Service service=serviceList.getServiceByHash(hash);
		service.addData(key,data);

		serviceMap.put(key,service.getName());
	}

	public String accessData(String  key){
		long hash=key.hashCode();

		return serviceList.getServiceByHash(hash).getData(key);
	}


	public void removeService(String serviceName){
		serviceList.removeService(new Service(serviceName,4));

		reallocation();
	}


	public void addService(String serviceName){
		serviceList.addService(new Service(serviceName,4));

		reallocation();
	}

	private void reallocation() {
		dataMap.forEach((k,v)->{

			long hash=k.hashCode();
			Service service=serviceList.getServiceByHash(hash);

			String serviceName=service.getName();

			if(serviceName.equals(serviceMap.get(k))){
				return;
			}

			serviceMap.put(k,serviceName);
		});
	}
}
