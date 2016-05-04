package com.kii.beehive.portal.helper;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.entitys.AuthInfo;
import com.kii.beehive.portal.exception.TokenTimeoutException;

/**
 * this class queries the url permission on the given user/token and constructs AuthInfoEntry to store these info
 *
 */
@Component
public class AuthInfoService {

    private Logger log= LoggerFactory.getLogger(AuthInfoService.class);

	private Map<String,AuthInfo> userTokenMap=new ConcurrentHashMap<>();


    public void createAuthInfoEntry(AuthInfo authInfo, String token) {

        log.debug("createAuthInfoEntry token: " + token + " for userID: " + authInfo.getUserID());

		userTokenMap.put(token,authInfo);

        return;
    }





	public AuthInfo getAuthInfoByToken(String token){


		AuthInfo info= userTokenMap.get(token);

		if(info.getExpireTime().getTime()<new Date().getTime()){
			userTokenMap.remove(token);
			throw new TokenTimeoutException();
		}

		return info;
	}

	public void removeToken(String token){

		userTokenMap.remove(token);

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
