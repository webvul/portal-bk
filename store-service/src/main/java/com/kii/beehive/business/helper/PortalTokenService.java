package com.kii.beehive.business.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.service.DeviceSupplierDao;
import com.kii.beehive.portal.store.entity.DeviceSupplier;
import com.kii.beehive.portal.store.entity.Token.PortalTokenType;
import com.kii.beehive.portal.store.entity.Token.TokenInfo;
import com.kii.extension.sdk.exception.UnauthorizedAccessException;

@Component
public class PortalTokenService {

	private ThreadLocal<TokenInfo> tokenLocal=ThreadLocal.withInitial(()->new TokenInfo("",PortalTokenType.Admin));


	@Autowired
	private DeviceSupplierDao supplierDao;

	public DeviceSupplier getSupplierInfo(){

		TokenInfo info=tokenLocal.get();

		if(info.getType()== PortalTokenType.UserSync) {
			return supplierDao.getSupplierByID(info.getToken());
		}else{
			throw new UnauthorizedAccessException();
		}
	}


	public String getUserDescription(){
		return tokenLocal.get().getDescription();
	}

	public void setToken(String token,PortalTokenType type) {
		this.tokenLocal.set(new TokenInfo(token,type));
	}

	public void cleanToken(){
		tokenLocal.remove();
	}

}
