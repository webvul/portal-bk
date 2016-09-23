package com.kii.beehive.obix.web.config;


import javax.annotation.PostConstruct;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import com.kii.beehive.obix.web.interceptor.DefineIntercepter;
import com.kii.beehive.obix.web.interceptor.NavigateIntercepter;

@EnableWebMvc
@Configuration
@ComponentScan(basePackages = {"com.kii.beehive.obix"},includeFilters = {
		@ComponentScan.Filter(type = FilterType.ANNOTATION, value =  {Component.class, Controller.class} )})
public class WebMVCConfig extends WebMvcConfigurerAdapter {

	private ObjectMapper mapper;


	@Autowired
	private DefineIntercepter defineIntercepter;

	@Autowired
	private NavigateIntercepter  navIntercepter;

	@PostConstruct
	public void init(){

		mapper=new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES,false);
		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);


		mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT,true);
		mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES,true);
		mapper.configure(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY,true);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,true);



	}

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {


		converters.add(new StringHttpMessageConverter());

		converters.add(createJsonMessageConverter());

		super.configureMessageConverters(converters);

	}

	private HttpMessageConverter<Object> createJsonMessageConverter() {

		MappingJackson2HttpMessageConverter jsonMarshaller = new MappingJackson2HttpMessageConverter();
		jsonMarshaller.setObjectMapper(mapper);

		return jsonMarshaller;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {

		registry.addInterceptor(defineIntercepter).addPathPatterns("/def");

		registry.addInterceptor(navIntercepter).addPathPatterns("/nav");


	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {

		registry.addMapping("/**")
				.allowedOrigins("*")
				.allowedMethods("GET","POST","PUT","DELETE");
	}

}