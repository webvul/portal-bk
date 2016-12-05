package com.kii.extension.sdk.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface BindAppByName {

	public String appName();

	public String  appBindSource() default "";

	public boolean usingDefault() default true;

	public boolean bindAdmin()  default true;

	public boolean bindThing()  default false;

	public boolean bindUser() default  false;

}
