package com.kii.beehive.obix.web.config;


import javax.annotation.PostConstruct;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"com.kii.beehive.obix.web"} ,includeFilters = {
		@ComponentScan.Filter(type = FilterType.ANNOTATION, value = RestController.class),
		@ComponentScan.Filter(type = FilterType.ANNOTATION, value = ControllerAdvice.class)
}
)
public class WebObixConfig extends WebMvcConfigurerAdapter {
	
	private ObjectMapper mapper;
	
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
	public void addCorsMappings(CorsRegistry registry) {
		
		registry.addMapping("/**")
				.allowedOrigins("*")
				.allowedMethods("GET","POST","PUT","DELETE");
	}
	
	@Override
	public void configureViewResolvers(ViewResolverRegistry registry) {
		registry.beanName();
	}
	
	@Bean
	public CommonsMultipartResolver getResolver() {
		return new CommonsMultipartResolver();
	}

//	@Bean
//	public GlobalHrefAdvice getAdvice(){
//		return new GlobalHrefAdvice();
//	}
	
}