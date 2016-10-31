package com.kii.beehive.portal.common.utils;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SafeThreadTool {

	private static final Logger log= LoggerFactory.getLogger(SafeThreadLocal.class);

	private static final AtomicInteger serial=new AtomicInteger(0);

	private static final Map<Integer,Long> tllMap =new ConcurrentHashMap<>();

	private static final List<SafeThreadLocal> localList=new CopyOnWriteArrayList<>();

	private static final ThreadLocal<Integer> uuidLoc = ThreadLocal.withInitial(() -> {

		Integer id=serial.getAndIncrement();
		log.debug("create new threadLoc value:"+id);
		tllMap.put(id,System.currentTimeMillis());
		return id;
	});

	public static  Integer  getUuid(){
		return  uuidLoc.get();
	}

	public static void addInst(SafeThreadLocal local){
		localList.add(local);
	}

	public static  void copyContext(ThreadHandler handler){

		localList.forEach((loc)->{

			Object obj=loc.entityMap.get(handler.threadID);
			if(obj!=null) {
				loc.entityMap.put(uuidLoc.get(), obj);
			}

		});


	}



	public static void removeLocalInfo(){

		Integer id=uuidLoc.get();

		uuidLoc.remove();

		localList.forEach((loc)->{
			loc.entityMap.remove(id);
		});
	}



	public static ThreadHandler getCurrThreadHandler(){

		return new ThreadHandler(SafeThreadTool.getUuid());
	}



	public static class ThreadHandler {

		private int threadID;

		private ThreadHandler(int id){
			this.threadID=id;
		}

	}





	public static void cleanOuttime(){

		Calendar cal=Calendar.getInstance();
		cal.add(Calendar.HOUR,-2);
		long timeStamp=cal.getTimeInMillis();

		Set<Integer> outtime=new HashSet<>();
		tllMap.forEach((k,v)->{
			if(v<timeStamp){
				outtime.add(k);
			}
		});

		localList.forEach((loc)->{
			loc.entityMap.keySet().removeAll(outtime);
		});
	}


}
