package com.kii.web.loader;

import java.io.File;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class WebEnvLoader {


	public static void main( String[] args ) throws Exception
	{


		Server server = new Server(7070);

		WebAppContext webapp = new WebAppContext();

		webapp.setContextPath("/mock");

		File warFile = new File("web-mock/src/main/webapp/");

		webapp.setWar(warFile.getAbsolutePath());

		server.setHandler(webapp);

		server.start();
//		server.dumpStdErr();
		server.join();
	}
}
