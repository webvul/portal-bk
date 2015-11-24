package mock.supplier.service;

import java.io.File;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;

public class SupplierService {

	public static void main( String[] args ) throws Exception
	{

		Server server = new Server(7080);


		ServletHandler  handler=new ServletHandler();

		handler.addServletWithMapping(SupplierServlet.class,"/user-sync");

		server.setHandler(handler);

		server.start();
		server.join();
	}
}
