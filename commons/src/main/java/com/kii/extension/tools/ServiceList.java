package com.kii.extension.tools;

import java.util.List;
import java.util.TreeMap;

public class ServiceList {


	private TreeMap<Long,Service> serviceMap=new TreeMap<>();


	public void initServiceList(List<Service> serviceList){

		for(Service s:serviceList){
			long[] hashs=s.getHashArray();

			for(long hash:hashs){
				serviceMap.put(hash,s);
			}
		}
	}

	public void addService(Service service){


		long[] hashs=service.getHashArray();

		for(long hash:hashs){
			serviceMap.put(hash,service);
		}


	}

	public void removeService(Service service){
		long[] hashs=service.getHashArray();

		for(long hash:hashs){
			serviceMap.remove(hash);
		}
	}

	public Service getServiceByHash(long hash){

		Long key=serviceMap.higherKey(hash);

		if(key==null){
			key=serviceMap.firstKey();
		}

		return serviceMap.get(key);

	}
}
