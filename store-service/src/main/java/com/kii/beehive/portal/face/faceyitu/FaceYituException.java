package com.kii.beehive.portal.face.faceyitu;

/**
 * Created by user on 16/9/23.
 */
public class FaceYituException extends RuntimeException {


	public FaceYituException(Exception e){
		super("yitu session expire, please try again later",e);
	}
	
	public FaceYituException(){
		super("yitu session expire, please try again later");
	}

}
