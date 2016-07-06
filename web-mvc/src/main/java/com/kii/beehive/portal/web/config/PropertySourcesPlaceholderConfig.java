package com.kii.beehive.portal.web.config;

import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.stereotype.Component;

/**
 * Created by hdchen on 6/30/16.
 */
@Component
@PropertySource(value = {"classpath:com/kii/beehive/portal/web/config.${spring.profile}.properties"})
public class PropertySourcesPlaceholderConfig extends PropertySourcesPlaceholderConfigurer {
}
