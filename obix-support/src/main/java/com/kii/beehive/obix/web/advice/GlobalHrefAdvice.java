package com.kii.beehive.obix.web.advice;


import java.io.IOException;
import java.lang.reflect.Type;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonInputMessage;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import com.fasterxml.jackson.annotation.JsonView;

import com.kii.beehive.obix.web.entity.ObixContain;

@ControllerAdvice("com.kii.beehive.obix.web.controller")
public class GlobalHrefAdvice extends RequestBodyAdviceAdapter {


	@Override
	public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
		return ObixContain.class.isAssignableFrom(methodParameter.getContainingClass());
	}

	@Override
	public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter methodParameter,
										   Type targetType, Class<? extends HttpMessageConverter<?>> selectedConverterType) throws IOException {

		JsonView annotation = methodParameter.getParameterAnnotation(JsonView.class);
		Class<?>[] classes = annotation.value();
		if (classes.length != 1) {
			throw new IllegalArgumentException(
					"@JsonView only supported for request body advice with exactly 1 class argument: " + methodParameter);
		}
		return new MappingJacksonInputMessage(inputMessage.getBody(), inputMessage.getHeaders(), classes[0]);
	}
}
