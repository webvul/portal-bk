package com.kii.beehive;

import java.io.File;

import org.apache.catalina.startup.Tomcat;


public class TomcatEnvLoader {


	public static void main( String[] args ) throws Exception
	{

		System.setProperty("spring.profile","local");

//		System.setProperty("log4j.configurationFile","log4j2.xml");
		String webappDirLocation = "./web-mvc/src/main/webapp/";
		Tomcat tomcat = new Tomcat();

		tomcat.setHostname("localhost");
		tomcat.setPort(9090);

		tomcat.addWebapp("/beehive-portal", new File(webappDirLocation).getAbsolutePath());

		tomcat.start();
		tomcat.getServer().await();
	}
}
