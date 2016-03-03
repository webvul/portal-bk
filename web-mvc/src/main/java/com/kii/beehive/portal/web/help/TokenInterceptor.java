//package com.kii.beehive.portal.web.help;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.servlet.ModelAndView;
//import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
//
//import com.kii.beehive.business.helper.PortalTokenService;
//import com.kii.beehive.portal.store.entity.Token.PortalTokenType;
//import UnauthorizedAccessException;
//
//public class TokenInterceptor extends HandlerInterceptorAdapter {
//
//
//
//	@Override
//	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
//			throws Exception {
//
//		String auth=request.getHeader("Authorization");
//
//		if(auth==null||!auth.startsWith("Bearer ")){
//
//			throw new UnauthorizedAccessException();
//		}
//
//		auth=auth.trim();
//
//		String token=auth.substring(auth.lastIndexOf(" ")+1).trim();
//
//
//
//
//		return true;
//
//	}
//
//	@Override
//	public void postHandle(
//			HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
//			throws Exception {
//
//		tokenService.cleanToken();
//	}
//}
