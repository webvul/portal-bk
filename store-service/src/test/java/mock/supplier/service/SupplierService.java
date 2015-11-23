package mock.supplier.service;

import java.io.File;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class SupplierService {

	public static void main( String[] args ) throws Exception
	{

//		System.setProperty("spring.profile","test");

		Server server = new Server(7080);

		WebAppContext webapp = new WebAppContext();

		webapp.setContextPath("/supplier-callback");

//		File warFile = new File("web-mvc/src/main/webapp/");

//		webapp.setWar(warFile.getAbsolutePath());

//		server.setHandler(webapp);

		webapp.addServlet(SupplierServlet.class,"/user-sync");


		server.start();
//		server.dumpStdErr();
		server.join();
	}
}
