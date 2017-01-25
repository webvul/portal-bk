package com.kii.extension.sdk.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.kii.extension.sdk.context.TokenBindTool;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface AppBindParam {

	String  appBindSource() default "";
	
	TokenBindTool.BindType tokenBind() default TokenBindTool.BindType.admin;
	
	String customBindName() default "";

}
