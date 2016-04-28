package com.kii.beehive.business.threaddemo;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class DemoThread {



	@Async
	public void doInAsync(String name){

		DemoMain.add("bar"+name);

		DemoMain.show();

	}




}
