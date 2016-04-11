//package com.kii.beehive.portal.helper;
//
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.cache.annotation.CachePut;
//import org.springframework.cache.annotation.Cacheable;
//import org.springframework.stereotype.Component;
//
//import com.kii.beehive.portal.common.utils.CollectUtils;
//import com.kii.beehive.portal.config.CacheConfig;
//import com.kii.beehive.portal.jdbc.dao.AuthInfoDao;
//import com.kii.beehive.portal.jdbc.dao.TeamDao;
//import com.kii.beehive.portal.jdbc.entity.AuthInfo;
//import com.kii.beehive.portal.jdbc.entity.Team;
//import com.kii.beehive.portal.store.entity.AuthInfoEntry;
//
///**
// * this class stores the auth info entities in DB and permanent token cache as below:
// * key: token
// * value: auth info entry
// *
// * for one user, there can be multiple tokens in DB and permanent token cache
// */
//@Component
//public class AuthInfoPermanentTokenService {
//
//    private Logger log= LoggerFactory.getLogger(AuthInfoPermanentTokenService.class);
//
//    @Autowired
//    private AuthInfoService authInfoService;
//
//    @Autowired
//    private AuthInfoDao authInfoDao;
//
//
//	@Autowired
//	private TeamDao teamDao;
//
//    /**
//     * get auth info entry from permanent token cache, if not found in permanent token cache, try to get from DB
//     *
//     * Important:
//     * this method should not be called inside this class, otherwise the cache function can't work properly
//     *
//     * @param token
//     * @return if valid token, return the corresponding AuthInfo entity; otherwise, return null
//     */
//    @Cacheable(cacheNames= CacheConfig.PERMANENT_TOKEN_CACHE, key="#token")
//    public AuthInfoEntry getAuthInfo(String token) {
//
//        log.debug("get(from cache/DB) token: " + token);
//
//        // get auth info from DB
//        List<AuthInfo> list = authInfoDao.findBySingleField(AuthInfo.TOKEN, token);
//        AuthInfo authInfo = CollectUtils.getFirst(list);
//        log.debug("authInfo: " + authInfo);
//
//        if(authInfo == null) {
//            return null;
//        }
//
//        // construct AuthInfoEntry
//        AuthInfoEntry authInfoEntry = authInfoService.createAuthInfoEntry(authInfo.getUserID(), authInfo.getTeamID(), token);
//
//        log.debug("authInfoEntry: " + authInfoEntry);
//
//        return authInfoEntry;
//    }
//
//    /**
//     * save auth info into permanent token cache and DB
//     *
//     * Important:
//     * this method should not be called inside this class, otherwise the cache function can't work properly
//     *
//     * @param userID
//     * @param token
//     */
//    @CachePut(cacheNames=CacheConfig.PERMANENT_TOKEN_CACHE, key="#token")
//    public AuthInfoEntry saveToken(String userID, String token) {
//
//        log.debug("save(into cache/DB) token: " + token + " for userID: " + userID);
//
//        Team team = teamDao.getTeamByUserID(userID);
//        Long teamId = null;
//        if(team != null){
//        	teamId = team.getId();
//        }
//
//        // save auth info into DB
//        AuthInfo authInfo = new AuthInfo();
//        authInfo.setUserID(userID);
//        authInfo.setToken(token);
//        authInfo.setTeamID(teamId);
//
//
//        long id = authInfoDao.insert(authInfo);
//        authInfo.setId(id);
//        log.debug("authInfo: " + authInfo);
//
//        // construct AuthInfoEntry
//        AuthInfoEntry authInfoEntry = authInfoService.createAuthInfoEntry(userID, teamId, token);
//
//        log.debug("authInfoEntry: " + authInfoEntry);
//
//        return authInfoEntry;
//    }
//
//    /**
//     * remove auth info from permanent token cache and DB
//     *
//     * Important:
//     * this method should not be called inside this class, otherwise the cache function can't work properly
//     *
//     * @param token
//     */
//    @CacheEvict(cacheNames=CacheConfig.PERMANENT_TOKEN_CACHE, key="#token")
//    public void removeToken(String token){
//        log.debug("remove(from cache/DB) token: " + token);
//
//        // remove auth info from DB
//        List<AuthInfo> list = authInfoDao.findBySingleField(AuthInfo.TOKEN, token);
//        AuthInfo authInfo = CollectUtils.getFirst(list);
//        log.debug("authInfo: " + authInfo);
//
//        if(authInfo != null) {
//            authInfoDao.deleteByID(authInfo.getId());
//        }
//
//    }
//
//    /**
//     * remove auth info from permanent token cache
//     *
//     * Important:
//     * this method should not be called inside this class, otherwise the cache function can't work properly
//     *
//     * @param token
//     */
//    @CacheEvict(cacheNames=CacheConfig.PERMANENT_TOKEN_CACHE, key="#token")
//    public void removeTokenFromCache(String token){
//        log.debug("remove(from cache/DB) token: " + token);
//    }
//
//    /**
//     * remove tokens from DB by userID
//     *
//     * @param userID
//     * @return token list from DB by userID
//     */
//    public List<String> removeTokenFromDBByUserID(String userID) {
//
//        log.debug("removeTokenByUserID: " + userID);
//
//        List<AuthInfo> list = authInfoDao.findBySingleField(AuthInfo.USER_ID, userID);
//
//        List<String> tokenList = new ArrayList<>();
//        for(AuthInfo authInfo : list) {
//            tokenList.add(authInfo.getToken());
//        }
//
//        authInfoDao.deleteByUserID(userID);
//
//        return tokenList;
//
//    }
//
//}
//
