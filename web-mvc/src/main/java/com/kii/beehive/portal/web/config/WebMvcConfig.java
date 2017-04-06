package com.kii.beehive.portal.web.config;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import com.kii.beehive.portal.web.controller.AuthInterceptor;
import com.kii.beehive.portal.web.controller.STOMPMessageController;
import com.kii.beehive.portal.web.controller.Security3PartyInterceptor;

@EnableWebMvc
@ComponentScan(basePackages = {"com.kii.beehive.portal.web.controller"},
		includeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION,classes = {Controller.class})},
		excludeFilters = {
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = STOMPMessageController.class)})
public class WebMvcConfig extends WebMvcConfigurerAdapter {

	@Autowired
	private AuthInterceptor authInterceptor;
	
	@Autowired
	private Security3PartyInterceptor security3PartyInterceptor;

	
	private  ObjectMapper getMapperInstance(){

		ObjectMapper mapper=new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);


		mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT,true);
		mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES,true);
		mapper.configure(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY,true);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,true);


		return mapper;
	}

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {


		converters.add(new StringHttpMessageConverter());

		converters.add(createJsonMessageConverter());

		super.configureMessageConverters(converters);

	}

	
	
	@Override
	public void configureViewResolvers(ViewResolverRegistry registry) {
		registry.beanName();
	}

	@Bean
	public CommonsMultipartResolver getResolver() {
		return new CommonsMultipartResolver();
	}

	private HttpMessageConverter<Object> createJsonMessageConverter() {

		MappingJackson2HttpMessageConverter jsonMarshaller = new MappingJackson2HttpMessageConverter();
		jsonMarshaller.setObjectMapper(getMapperInstance());

		return jsonMarshaller;
	}


	@Bean(name = "jsonView")
	public View getJsonView() {

		MappingJackson2JsonView view = new MappingJackson2JsonView();
		view.setUpdateContentLength(true);
		return view;
	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		
		registry.addInterceptor(authInterceptor).addPathPatterns("/**").excludePathPatterns("/plugin/**");
	}
	
	
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedOrigins("*").allowedMethods("GET", "PUT", "POST", "DELETE", "HEAD").allowCredentials(false);
	}
	
	@Override
	public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
		
		configurer.setDefaultTimeout(60 * 1000l);
		
		
	}
}
