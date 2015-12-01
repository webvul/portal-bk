package com.kii.beehive.portal.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.service.DeviceSupplierDao;
import com.kii.beehive.portal.store.entity.DeviceSupplier;
import com.kii.extension.sdk.exception.UnauthorizedAccessException;

@Component
public class PortalTokenService {

	private ThreadLocal<TokenInfo> tokenLocal=new ThreadLocal<>();


	@Autowired
	private DeviceSupplierDao supplierDao;

	public DeviceSupplier getSupplierInfo(){

		TokenInfo info=tokenLocal.get();

		if(info.type== PortalTokenType.UserSync) {
			return supplierDao.getSupplierByID(info.token);
		}else{
			throw new UnauthorizedAccessException();
		}
	}


	public PortalTokenType getTokenType(){
		return tokenLocal.get().type;
	}

	public String getUserDescription(){
		return tokenLocal.get().getDescription();
	}

	public void setToken(String token,PortalTokenType type) {
		this.tokenLocal.set(new TokenInfo(token,type));
	}

	private static class TokenInfo{
		private String token;
		private PortalTokenType type;

		public TokenInfo(String token,PortalTokenType type){
			this.token=token;
			this.type=type;
		}

		public String getDescription(){
			return type.name()+":"+token;
		}
	}

	public enum PortalTokenType {

		UserSync,Demo;

	}
}
