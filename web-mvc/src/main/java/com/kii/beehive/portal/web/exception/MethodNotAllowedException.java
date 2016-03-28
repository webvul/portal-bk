package com.kii.beehive.portal.web.exception;

import com.kii.beehive.portal.web.constant.ErrorCode;
import org.springframework.http.HttpStatus;

/**
 * Created by mac on 16/3/28.
 */
public class MethodNotAllowedException  extends PortalException{
    public MethodNotAllowedException(String msg){
        super(ErrorCode.METHOD_NOT_ALLOWED,msg, HttpStatus.METHOD_NOT_ALLOWED);
    }
}
