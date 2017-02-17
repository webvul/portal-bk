package com.kii.beehive.business.schedule;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class QuartzFactory {
	
	
	
	SchedulerFactory schedFact = new StdSchedulerFactory();
	
	@Bean
	public Scheduler getScheduler() throws SchedulerException {
		
		Scheduler sched = schedFact.getScheduler();
		
		sched.start();
		return sched;
		
	}
}
