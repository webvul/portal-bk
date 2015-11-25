package mock.supplier.service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;

public class SupplierServlet extends HttpServlet {

	private static Logger log= LoggerFactory.getLogger(SupplierServlet.class);


	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException
	{


		String content= StreamUtils.copyToString(req.getInputStream(), Charsets.UTF_8);

		ObjectMapper mapper=new ObjectMapper();

		Map<String,Object> result=mapper.readValue(content,Map.class);

		String userID= (String) result.get("userID");

		String type= (String) result.get("type");

		int retryCount=(Integer)result.get("retryCount");

		log.debug("userID :{}  type: {}  retry: {} ",userID,type,retryCount);

		if(!userID.startsWith("user")){

			resp.setStatus(500);
			return;
		}

		int id=Integer.parseInt(userID.substring(userID.lastIndexOf("-")+1));

		if(id%5==0&retryCount<3){
			resp.setStatus(501);
			return;
		}

		resp.setStatus(200);

		return;





	}

}
