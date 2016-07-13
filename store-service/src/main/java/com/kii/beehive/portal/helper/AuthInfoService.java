package com.kii.beehive.portal.helper;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.entitys.AuthInfo;
import com.kii.beehive.portal.exception.TokenTimeoutException;
import com.kii.beehive.portal.exception.UnauthorizedException;
import com.kii.beehive.portal.jdbc.entity.BeehiveJdbcUser;
import com.kii.extension.sdk.annotation.BindAppByName;
import com.kii.extension.sdk.context.UserTokenBindTool;

/**
 * this class queries the url permission on the given user/token and constructs AuthInfoEntry to store these info
 *
 */
@Component
@BindAppByName(appName="portal",appBindSource="propAppBindTool")
public class AuthInfoService {

    private Logger log= LoggerFactory.getLogger(AuthInfoService.class);

	private Map<String,AuthInfo> userTokenMap=new ConcurrentHashMap<>();


	@Autowired
	private UserTokenBindTool tokenBind;

    public void createAuthInfoEntry(AuthInfo authInfo, String token) {

        log.debug("createAuthInfoEntry token: " + token + " for userID: " + authInfo.getUserID());

		userTokenMap.put(token,authInfo);

        return;
    }




	public void bindUser(BeehiveJdbcUser user){

		tokenBind.bindUserInfo(user.getUserID(),user.getUserPassword());

	}





	public AuthInfo getAuthInfoByToken(String token){


		AuthInfo info= userTokenMap.get(token);

		if(info==null){
			return null;
		}
		if(info.getExpireTime().getTime()<new Date().getTime()){
			userTokenMap.remove(token);
			throw new TokenTimeoutException(token);
		}

		return info;
	}

	public void removeToken(String token){

		AuthInfo  info=userTokenMap.remove(token);

		if(info==null){
			UnauthorizedException excep=  new UnauthorizedException(UnauthorizedException.LOGIN_TOKEN_INVALID);
			excep.addParam("token",token);
			throw excep;
		}
	}
	
	
	public void removeTokenByUserID(String id) {

		List<String> tokens=userTokenMap.entrySet().stream().filter((entry)->{
					AuthInfo info=entry.getValue();
					return !info.is3Party()&&info.getUserID().equals(id);
				})
				.map((entry)->entry.getKey()).collect(Collectors.toList());

		userTokenMap.keySet().removeAll(tokens);

	}
}
