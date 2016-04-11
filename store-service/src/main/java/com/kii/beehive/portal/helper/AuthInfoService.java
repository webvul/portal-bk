package com.kii.beehive.portal.helper;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.exception.TokenTimeoutException;
import com.kii.beehive.portal.exception.UnauthorizedException;
import com.kii.beehive.portal.jdbc.dao.AuthInfoDao;
import com.kii.beehive.portal.jdbc.entity.AuthInfo;

/**
 * this class queries the url permission on the given user/token and constructs AuthInfoEntry to store these info
 *
 */
@Component
public class AuthInfoService {

    private Logger log= LoggerFactory.getLogger(AuthInfoService.class);


	@Autowired
	private AuthInfoDao authInfoDao;

	private Map<String,AuthInfo> userTokenMap=new ConcurrentHashMap<>();

	@Scheduled(cron="0 0 1 * * ?")
	public void checkTTL(){
		//TODO:
	}


    public void createAuthInfoEntry(AuthInfo auth, String token,boolean isPermanentToken) {

        log.debug("createAuthInfoEntry token: " + token + " for userID: " + auth);

		userTokenMap.put(token,auth);

		if(isPermanentToken) {

			authInfoDao.insert(auth);
		}
        return;
    }


	public AuthInfo getAuthInfoByToken(String token){
		AuthInfo info= userTokenMap.computeIfAbsent(token,(t)-> authInfoDao.getAuthInfoByToken(t));

		if(info==null){
			throw new UnauthorizedException("token invalid ");
		}
		if(info.getExpireTime().getTime()<new Date().getTime()){
			userTokenMap.remove(token);
			throw new TokenTimeoutException();
		}

		return info;
	}

	public void removeToken(String token){

		userTokenMap.remove(token);

		authInfoDao.deleteByToken(token);

	}
	
	
	public void removeTokenByUserID(String id) {

		List<String> tokens=userTokenMap.entrySet().stream().filter((entry)->{
			return entry.getValue().getUserID().equals(id);
		}).map((entry)->entry.getKey()).collect(Collectors.toList());

		userTokenMap.keySet().removeAll(tokens);

		authInfoDao.deleteByUserID(id);

	}
}
