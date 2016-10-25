package com.kii.beehive.obix.web.advice;


import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.kii.beehive.obix.web.entity.ObixContain;

@ControllerAdvice(basePackages={"com.kii.beehive.obix.web.controller"})
public class GlobalHrefAdvice implements ResponseBodyAdvice<ObixContain> {



	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		return ObixContain.class.isAssignableFrom(returnType.getParameterType());
	}

	@Override
	public ObixContain beforeBodyWrite(ObixContain body, MethodParameter returnType, MediaType selectedContentType,
									   Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
		if(!(body instanceof  ObixContain)){
			return body;
		}

		ObixContain obix=(ObixContain)body;

		String url=request.getURI().toString();

		obix.setHref(url);

		return obix;
	}
}
