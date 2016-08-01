package com.kii.beehive.portal.web.security;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.manager.TagThingManager;
import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.common.utils.ThingIDTools;

/**
 * Created by hdchen on 8/1/16.
 */
@Component
public class AccessController {
	private static final Logger LOG = LoggerFactory.getLogger(AccessController.class);

	@Autowired
	private TagThingManager tagThingManager;

	public boolean canSubscribeAllThingStatus(String appId) {
		Set<? extends GrantedAuthority> authorities = getAuthorities();
		if (authorities.contains(new SimpleGrantedAuthority(Role.administrator.name()))) {
			return true;
		}
		return false;
	}

	public boolean canSubscribeThingStatus(String appId, String thingId) {
		Set<? extends GrantedAuthority> authorities = getAuthorities();
		if (!authorities.contains(new SimpleGrantedAuthority(Role.administrator.name())) &&
				!authorities.contains(new SimpleGrantedAuthority(Role.userAdmin.name()))) {
			if (!tagThingManager.isKiiThingOwner(ThingIDTools.joinFullKiiThingID(appId, thingId))) {
				LOG.info(new StringBuffer("Current user ")
						.append(AuthInfoStore.getUserID())
						.append(" is not the owner. ")
						.append("appId = ")
						.append(appId)
						.append(", thingId = ")
						.append(thingId)
						.toString());
				return false;
			}
		}
		return true;
	}

	private Set<? extends GrantedAuthority> getAuthorities() {
		return new HashSet(Optional.ofNullable(SecurityContextHolder.getContext()
				.getAuthentication().getAuthorities()).orElse(Collections.emptyList()));
	}
}
