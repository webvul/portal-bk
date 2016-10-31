package com.kii.beehive.portal.common.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class SafeThreadLocal<T> {



	private SafeThreadLocal(Supplier<? extends T> initFunction){

		this.initFunction=initFunction;
	}

	public static <T> SafeThreadLocal<T>  withInitial(Supplier<? extends T> supplier){

		SafeThreadLocal<T> inst=new SafeThreadLocal<>(supplier);

		SafeThreadTool.addInst(inst);

		return inst;
	}

	public static <T>  SafeThreadLocal<T> getInstance(){

		return withInitial(null);
	}

	private final Supplier<? extends T> initFunction;


	Map<Integer,T>  entityMap=new ConcurrentHashMap<>();


	public T get(){

		Integer  uuid=SafeThreadTool.getUuid();
		if(initFunction==null) {
			return entityMap.get(uuid);
		}else{

			return entityMap.computeIfAbsent(uuid,(k)->initFunction.get());
		}
	}

	public void set(T  obj){
		if(obj==null){
			return;
		}
		entityMap.put(SafeThreadTool.getUuid(),obj);
	}

	public T remove(){

		return entityMap.remove(SafeThreadTool.getUuid());

	}








}
