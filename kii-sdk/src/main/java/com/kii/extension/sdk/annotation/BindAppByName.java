package com.kii.extension.sdk.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.kii.extension.sdk.context.TokenBindTool;

@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface BindAppByName {

	public String appName();

	public String  appBindSource() default "";

	public TokenBindTool.BindType tokenBind() default TokenBindTool.BindType.admin;
	
	public String customBindName() default "";


}
