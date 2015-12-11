package com.kii.beehive.portal.jdbc.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JdbcField {

	String column();

	JdbcFieldType type() default JdbcFieldType.Auto;
}
