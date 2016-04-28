package com.kii.beehive.business.threaddemo;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:com/kii/beehive/business/testSchedule.xml" })
public class SpringExecuteMaintain {


	@Autowired
	@Qualifier("myExecutor")
	private ThreadPoolTaskExecutor springExecutor;


	@Autowired
	private DemoMain main;

	@Test
	public void doClean() throws IOException, InterruptedException {

		main.doWork("a");

		Thread.sleep(1000l);
		main.show();


		main.doWork("b");
		Thread.sleep(1000l);

		main.show();

		main.doClean();
		Thread.sleep(1000l);

		main.show();

		System.in.read();


	}


}
