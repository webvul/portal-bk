//package com.kii.beehive.portal.web.help;
//
//import java.util.Enumeration;
//import java.util.LinkedList;
//import java.util.List;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
//
//import com.kii.beehive.business.helper.OpLogTools;
//import com.kii.beehive.portal.jdbc.entity.AuthInfo;
//import com.kii.beehive.business.manager.AuthManager;
//import com.kii.beehive.portal.web.constant.Constants;
//
//public class OperatorLogInterceptor extends HandlerInterceptorAdapter {
//
//
//
//
//	@Autowired
//    private AuthManager authManager;
//
//	@Override
//	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
//			throws Exception {
//		this.logRequest(request);
//
//		List<String> list=new LinkedList<>();
//
//
//		String auth = request.getHeader(Constants.ACCESS_TOKEN);
//
//		if (auth != null && auth.startsWith("Bearer ")) {
//			auth = auth.trim();
//
//			String token = auth.substring(auth.lastIndexOf(" ") + 1).trim();
//			AuthInfo authInfo = authManager.getAuthInfo(token);
//			if(authInfo == null){
//				list.add("");
//			}else{
//				list.add(authInfo.getUserID());
//			}
//
//		}
//		list.add(request.getRequestURI());
//
//		logTool.write(list);
//
//		return super.preHandle(request, response, handler);
//	}
//
//	/**
//	 * log out uri, uri params and headers into system log
//	 * @param request
//     */
//
//}
