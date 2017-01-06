package com.kii.beehive.portal.store.test;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.portal.service.thing.CommandsDao;
import com.kii.extension.sdk.entity.thingif.ThingCommand;

public class TestCommandGet extends TestTemplate{
	
	@Autowired
	private CommandsDao dao;
	
	@Test
	public void test(){
		
		List<ThingCommand> cmdList= dao.queryCommand("portal","th.f83120e36100-1c9a-6e11-de2d-0685a49f",null,new Date().getTime());
		
		System.out.println(cmdList);
	}
	
	
}
