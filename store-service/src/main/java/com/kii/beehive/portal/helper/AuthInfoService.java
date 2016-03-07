package com.kii.beehive.portal.helper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.jdbc.dao.GroupUserRelationDao;
import com.kii.beehive.portal.jdbc.dao.PermissionDao;
import com.kii.beehive.portal.jdbc.entity.GroupUserRelation;
import com.kii.beehive.portal.jdbc.entity.Permission;
import com.kii.beehive.portal.store.entity.AuthInfoEntry;

/**
 * this class queries the url permission on the given user/token and constructs AuthInfoEntry to store these info
 *
 */
@Component
public class AuthInfoService {

    private Logger log= LoggerFactory.getLogger(AuthInfoService.class);

    @Autowired
    private GroupUserRelationDao groupUserRelationDao;
    
    @Autowired
    private PermissionDao permissionDao;

    /**
     * query the url permission on the given user/token and construct AuthInfoEntry to store these info
     *
     * @param userID
     * @param token
     */
    public AuthInfoEntry createAuthInfoEntry(String userID,Long teamID, String token) {

        log.debug("createAuthInfoEntry token: " + token + " for userID: " + userID);

        //get user Permission
        Set<String> pSet = new HashSet<String>();
        List<GroupUserRelation> groupUserRelation = groupUserRelationDao.findByUserID(userID);
        for(GroupUserRelation gur:groupUserRelation){
        	List<Permission> plist = permissionDao.findByUserGroupID(gur.getUserGroupID());
        	for(Permission p:plist){
                log.debug("get permission on userID: " + userID + " action: " + p.getAction());
        		pSet.add(p.getAction());
        	}
        }

        return new AuthInfoEntry(userID, teamID, token, pSet);
    }


}
