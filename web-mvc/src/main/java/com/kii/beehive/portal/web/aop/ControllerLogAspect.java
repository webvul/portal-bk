package com.kii.beehive.portal.web.aop;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Aspect
public class ControllerLogAspect {

    @Autowired
    private ObjectMapper mapper;


    private Logger log= LoggerFactory.getLogger(ControllerLogAspect.class);

    @Pointcut("within ( com.kii.beehive.portal.web.controller.* ) ")
    private void bindController(){

    };


    @Before("bindController()")
    public void beforeCallBusinessFun(JoinPoint joinPoint){

        try {
            log.info("*************** Controller Method Start ***************");
            Method method = logMethod(joinPoint);

            StringBuilder sb = new StringBuilder("* Params: ");

            Parameter[] params = method.getParameters();
            Object[] args = joinPoint.getArgs();
            if( args != null) {
                for (int i = 0; i < args.length; i++) {
                    String name = params[i].getName();
                    Object val = args[i];
                    sb.append(" <").append(name).append("=").append(safeToString(val)).append(">");
                }
            }
            log.info(sb.toString());
            log.info("*******************************************************");
        }catch(Throwable ex){
            log.error("Exception in beforeCallBusinessFun", ex);
            return;
        }

    }

    private Method logMethod(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        log.info("* Method: "+method);
        return method;
    }

    @AfterReturning(pointcut = "bindController()",   returning = "result" )
    public void afterCallBusinessFun(JoinPoint joinPoint,Object result){

        try {
            log.info("*************** Controller Method END ***************");
            logMethod(joinPoint);

            log.info("* Return: "+ safeToString(result));
            log.info("*****************************************************");
        }catch(Throwable ex){
            log.error("Exception in afterCallBusinessFun", ex);
            return;
        }
    }

    private String safeToString(Object obj){

        if(obj==null){
            return "null";
        }

        if(obj.getClass().isPrimitive()){
            return String.valueOf(obj);
        }else if(obj instanceof String ){
            return String.valueOf(obj);
        }else if(obj.getClass().isArray()){
            Object[] objArray=(Object[])obj;

            return Arrays.deepToString(objArray);


        }else if(obj.getClass().getPackage().getName().startsWith("com.kii")){
            try {
                return mapper.writeValueAsString(obj);
            } catch (JsonProcessingException e) {
                log.error(e.getMessage(), e);
                return "";
            }

        }else{
            return String.valueOf(obj);
        }

    }

}
