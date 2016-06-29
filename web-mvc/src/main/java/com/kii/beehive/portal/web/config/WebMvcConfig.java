package com.kii.beehive.portal.web.config;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.ImportResource;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.portal.web.controller.STOMPMessageController;
import com.kii.beehive.portal.web.help.AuthInterceptor;

@EnableWebMvc
@Configuration
@ImportResource("classpath:com/kii/beehive/portal/web/portalContext.xml")
@ComponentScan(basePackages = {"com.kii.beehive.portal.web.controller"}, excludeFilters = {
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = STOMPMessageController.class)})
public class WebMvcConfig extends WebMvcConfigurerAdapter {

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private AuthInterceptor authInterceptor;

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {


		converters.add(new StringHttpMessageConverter());

		converters.add(createJsonMessageConverter());

		super.configureMessageConverters(converters);

	}


	@Override
	public void addInterceptors(InterceptorRegistry registry) {

		registry.addInterceptor(authInterceptor).addPathPatterns("/**");

	}

	@Override
	public void configureViewResolvers(ViewResolverRegistry registry) {

		registry.beanName();

	}




	@Bean(name="jsonView")
	public View getJsonView() {

		MappingJackson2JsonView view=new MappingJackson2JsonView();
		view.setUpdateContentLength(true);
		return view;
	}



	@Bean(name="multipartResolver")
	public CommonsMultipartResolver getResolver() {
		return new CommonsMultipartResolver();
	}

	private HttpMessageConverter<Object> createJsonMessageConverter() {

		MappingJackson2HttpMessageConverter jsonMarshaller = new MappingJackson2HttpMessageConverter();
		jsonMarshaller.setObjectMapper(mapper);

		return jsonMarshaller;
	}

}
