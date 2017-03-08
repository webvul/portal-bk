package com.kii.beehive.business.helper;


import org.apache.logging.log4j.core.config.Configurator;


public class LogConfigLoader {


//
//	@Value("${spring.profile}")
//	private String profile;
//
//
//	@Autowired
//	private ResourceLoader loader;
//
//	private Logger log= LoggerFactory.getLogger(LogConfigLoader.class);
	
	
	public LogConfigLoader() {
		
		
		String profile = System.getProperty("spring.profile");
		
		//			String path = loader.getResource("classpath:com/kii/beehive/portal/web/log4j."+profile+".xml").getFile().getAbsolutePath();
		
		Configurator.initialize("beehive", LogConfigLoader.class.getClassLoader(), "com/kii/beehive/portal/web/log4j." + profile + ".xml");
		
	}
}
