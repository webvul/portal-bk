package com.kii.beehive.obix;

import java.io.File;

import org.apache.catalina.startup.Tomcat;


public class ObixEnvLoader {


	public static void main( String[] args ) throws Exception
	{

		System.setProperty("spring.profile","local");

		System.setProperty("log4j.configurationFile", "log4j.local.xml");
		String webappDirLocation = "./obix-support/src/main/webapp/";
		Tomcat tomcat = new Tomcat();

		tomcat.setHostname("localhost");
		tomcat.setPort(9090);

		tomcat.addWebapp("/beehive-obix", new File(webappDirLocation).getAbsolutePath());

		tomcat.start();
		tomcat.getServer().await();
	}
}
