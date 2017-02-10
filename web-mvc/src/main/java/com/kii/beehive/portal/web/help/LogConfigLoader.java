package com.kii.beehive.portal.web.help;


import javax.annotation.PostConstruct;

import org.apache.logging.log4j.core.config.Configurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
public class LogConfigLoader {
	
	
	
	@Value("${spring.profile}")
	private String profile;
	
	
	@Autowired
	private ResourceLoader loader;
	
	private Logger log= LoggerFactory.getLogger(LogConfigLoader.class);
	
	
	@PostConstruct
	public void init(){
		
		//			String path = loader.getResource("classpath:com/kii/beehive/portal/web/log4j."+profile+".xml").getFile().getAbsolutePath();
		
		Configurator.initialize("beehive",this.getClass().getClassLoader(),"com/kii/beehive/portal/web/log4j."+profile+".xml");
		
	}
}
