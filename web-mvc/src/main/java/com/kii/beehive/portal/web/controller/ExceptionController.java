package com.kii.beehive.portal.web.controller;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonMappingException;

import com.kii.beehive.portal.common.utils.CollectUtils;
import com.kii.beehive.portal.common.utils.StrTemplate;
import com.kii.beehive.portal.exception.BusinessException;
import com.kii.beehive.portal.sysmonitor.SysMonitorMsg;
import com.kii.beehive.portal.sysmonitor.SysMonitorQueue;
import com.kii.beehive.portal.web.help.I18nPropertyTools;
import com.kii.extension.sdk.exception.KiiCloudException;

@ControllerAdvice(basePackages = "com.kii.beehive.portal.web.controller")
public class ExceptionController {

	private Logger log = LoggerFactory.getLogger(ExceptionController.class);

	@Autowired
	private ResourceLoader loader;

	@Autowired
	private I18nPropertyTools tool;

	private static MultiValueMap<String, String> headers=new LinkedMultiValueMap<>();

	static{
		headers.add("Content-Type","application/json;charset=UTF-8");
	}


	@ExceptionHandler(Throwable.class)
	public ResponseEntity<Object> handleGlobalException(Throwable ex) {

		log.error("global exception ", ex);
		
		if(ex instanceof DataAccessException ||
				(ex.getCause()!=null&& SQLException.class.isAssignableFrom(ex.getCause().getClass()) )){
			
			Exception excep=(Exception)ex;
			if(! (ex instanceof  DataAccessException)) {
				excep= (Exception) ex.getCause();
			}
			
			SysMonitorMsg notice=new SysMonitorMsg();
			notice.setFrom(SysMonitorMsg.FromType.DB);
			notice.setErrMessage(excep.getMessage());
			notice.setErrorType(excep.getClass().getName());
			
			SysMonitorQueue.getInstance().addNotice(notice);
		}

		Map<String, Object> errorMap = new HashMap<>();
		errorMap.put("errorCode", ex.getClass().getSimpleName());
		errorMap.put("errorMessage", ex.getMessage());

		StackTraceElement[]  traceList=ex.getStackTrace();

		errorMap.put("stackTrace",traceList);

		ResponseEntity<Object> resp = new ResponseEntity(errorMap, headers,HttpStatus.INTERNAL_SERVER_ERROR);
		return resp;
	}
	
	
	

	private List<String> filter = CollectUtils.createList("status", "suppressed", "stackTrace", "class", "localizedMessage");

	private Map<String,Object> convertExeptionToJson(RuntimeException ex) {

		Map<String, Object> error = new HashMap<>();

		for (PropertyDescriptor desc : BeanUtils.getPropertyDescriptors(ex.getClass())) {

			if (!filter.contains(desc.getDisplayName())) {
				try {
					Method method = desc.getReadMethod();
					if (method.isAnnotationPresent(JsonIgnore.class)) {
						continue;
					}
					Object val = desc.getReadMethod().invoke(ex, null);
					if (val == null) {
						continue;
					}
					error.put(desc.getDisplayName(), val);
				} catch (IllegalAccessException | InvocationTargetException e) {
					log.error("exception convert to json fail", e);
					throw new IllegalArgumentException(e);
				}
			}
		}

		return error;
	}


	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<Object> handleStoreServiceException(BusinessException ex) {

		log.error("store exception ", ex);

		Map<String,Object> error = getErrorInfoInJson(ex);

		ResponseEntity<Object> resp = new ResponseEntity(error,headers, HttpStatus.valueOf(ex.getStatusCode()));


		return resp;
	}


	private Locale  locale=Locale.ENGLISH;

	//TODO:just for test,in real env
	//the locale should get from user's input
	public void setLocale(Locale local){
		this.locale=local;
	}

	private Map<String,Object> getErrorInfoInJson(BusinessException ex) {


		Map<String, Object> error = new HashMap<>();

		String code=ex.getErrorCode();

		String fullCode=ex.getClass().getName()+"."+code;

		I18nPropertyTools.PropertyEntry  entry=tool.getPropertyEntry("error.errorMessage", locale);

		String msgTemplate=entry.getPropertyValue(fullCode);

		String  msg= StrTemplate.generByMap(msgTemplate,ex.getParamMap());

		error.put("errorMessage",msg);
		error.put("errorCode",code);
		error.put("errorParam",ex.getParamMap());

		return error;
	}



	@ExceptionHandler(KiiCloudException.class)
	public ResponseEntity<Object> handleKiiCloudException(KiiCloudException ex) {

		log.error("kiicloud exception ", ex);

		Map<String,Object> error = convertExeptionToJson(ex);
		error.put("from","KiiCloud");

		HttpStatus status = HttpStatus.valueOf(ex.getStatusCode());
		error.put("kiiAppStatus",status);
		error.put("kiiAppRespBody",ex.getResponseBody());
		
		ResponseEntity<Object> resp = new ResponseEntity(error,headers, HttpStatus.INTERNAL_SERVER_ERROR);
		return resp;
	}


	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<Object>  HandleJsonFormatException(HttpMessageNotReadableException ex){

		log.error("http message readable exception ", ex);

		Throwable excep=ex.getCause();

		Map<String, Object> error = new HashMap<>();
		if(excep instanceof JsonMappingException) {
			error.put("errorCode", "INPUT_PARAM_JSON_FORMAT_ERROR");
			error.put("errorMessage",((JsonMappingException) excep).getMessage());
		}else{
			error.put("errorCode", "INPUT_REQUEST_ERROR");
			error.put("errorMessage", ex.getMessage());
		}
		ResponseEntity<Object> resp = new ResponseEntity(error,headers, HttpStatus.BAD_REQUEST);
		return resp;
	}
}
