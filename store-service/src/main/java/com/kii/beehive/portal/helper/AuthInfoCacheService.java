package com.kii.beehive.portal.helper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.config.CacheConfig;
import com.kii.beehive.portal.jdbc.dao.GroupUserRelationDao;
import com.kii.beehive.portal.jdbc.dao.PermissionDao;
import com.kii.beehive.portal.jdbc.entity.GroupUserRelation;
import com.kii.beehive.portal.jdbc.entity.Permission;
import com.kii.beehive.portal.store.entity.AuthInfoEntry;

/**
 * this class stores the auth info entries in auth info cache as below:
 * key: token
 * value: auth info entry
 *
 */
@Component
public class AuthInfoCacheService {

    private Logger log= LoggerFactory.getLogger(AuthInfoCacheService.class);

    @Autowired
    private AuthInfoService authInfoService;

    /**
     * get auth info entry from auth info cache
     *
     * Important:
     * this method should not be called inside this class, otherwise the cache function can't work properly
     *
     * @param token
     * @return if valid token, return the corresponding AuthInfo entity; otherwise, return null
     */
    @Cacheable(cacheNames=CacheConfig.AUTH_INFO_CACHE, key="#token")
    public AuthInfoEntry getAuthInfo(String token) {
        return null;
    }

    /**
     * save auth info into auth info cache
     * if there is existing auth info in auth info cache, will update the token inside auth info
     *
     * Important:
     * this method should not be called inside this class, otherwise the cache function can't work properly
     *
     * @param userID
     * @param token
     */
    @CachePut(cacheNames=CacheConfig.AUTH_INFO_CACHE, key="#token")
    public AuthInfoEntry saveToken(String userID, String token) {

        log.debug("save(into cache) token: " + token + " for userID: " + userID);

        return authInfoService.createAuthInfoEntry(userID, token);
    }

    /**
     * remove auth info entry from auth info cache
     *
     * Important:
     * this method should not be called inside this class, otherwise the cache function can't work properly
     *
     * @param token
     */
    @CacheEvict(cacheNames=CacheConfig.AUTH_INFO_CACHE, key="#token")
    public void removeToken(String token){
        log.debug("remove(from cache) token: " + token);
    }

}
