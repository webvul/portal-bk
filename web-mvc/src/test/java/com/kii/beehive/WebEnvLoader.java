package com.kii.beehive;

import java.io.File;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class WebEnvLoader {


	public static void main( String[] args ) throws Exception
	{

		System.setProperty("spring.profile","test");

		Server server = new Server(8080);

		WebAppContext webapp = new WebAppContext();

		webapp.setContextPath("/");

		File warFile = new File("web-mvc/src/main/webapp/");

		webapp.setWar(warFile.getAbsolutePath());

		server.setHandler(webapp);

		server.start();
//		server.dumpStdErr();
		server.join();
	}
}
