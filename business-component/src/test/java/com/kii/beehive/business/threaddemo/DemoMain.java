package com.kii.beehive.business.threaddemo;

import java.lang.ref.Reference;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DemoMain {

	private static ThreadLocal<List<String>>  local=ThreadLocal.withInitial(()->{
		System.out.println("new thread local"+Thread.currentThread().getName());
		return new ArrayList<String>();
	});


	public static void add(String val){
		local.get().add(val);
	}

	public static void clean(){
		local.remove();
	}

	public static List<String> getValues(){
		return local.get();
	}

	@Autowired
	private DemoThread  thread;

	public void doWork(String name){

		add("foo"+name);

		thread.doInAsync(name);


	}
	
	public void doClean() {


		local.remove();

	}



	public static   void show() {
		try {
			// Get a reference to the thread locals table of the current thread
			Thread thread = Thread.currentThread();
			Field threadLocalsField = Thread.class.getDeclaredField("threadLocals");
			threadLocalsField.setAccessible(true);
			Object threadLocalTable = threadLocalsField.get(thread);

			// Get a reference to the array holding the thread local variables inside the
			// ThreadLocalMap of the current thread
			Class threadLocalMapClass = Class.forName("java.lang.ThreadLocal$ThreadLocalMap");
			Field tableField = threadLocalMapClass.getDeclaredField("table");
			tableField.setAccessible(true);
			Object table = tableField.get(threadLocalTable);

			// The key to the ThreadLocalMap is a WeakReference object. The referent field of this object
			// is a reference to the actual ThreadLocal variable
			Field referentField = Reference.class.getDeclaredField("referent");
			referentField.setAccessible(true);

			System.out.println("thread local dump:");
			for (int i = 0; i < Array.getLength(table); i++) {
				// Each entry in the table array of ThreadLocalMap is an Entry object
				// representing the thread local reference and its value
				Object entry = Array.get(table, i);
				if (entry != null) {
					// Get a reference to the thread local object and remove it from the table
					ThreadLocal threadLocal = (ThreadLocal)referentField.get(entry);
					System.out.print(threadLocal.get()+" :  ");

					threadLocal.remove();
				}
			}
			System.out.println();

		} catch(Exception e) {
			// We will tolerate an exception here and just log it
			throw new IllegalStateException(e);
		}
	}

}
