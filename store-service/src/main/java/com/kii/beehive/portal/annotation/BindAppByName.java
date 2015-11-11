package com.kii.beehive.portal.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@BindApp
@Inherited
public @interface BindAppByName {

	public String appName();

	public String  appBindSource() default "";

	public boolean usingDefault() default true;

}
