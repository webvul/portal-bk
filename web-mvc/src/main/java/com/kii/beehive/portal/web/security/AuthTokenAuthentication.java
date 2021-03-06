package com.kii.beehive.portal.web.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.kii.beehive.portal.entitys.AuthUser;

/**
 * Created by hdchen on 7/13/16.
 */
public class AuthTokenAuthentication implements Authentication {
	private final String principal;

	private final AuthUser userDetail;

	private List<GrantedAuthority> authorityList = new ArrayList();

	private String authToken;

	private boolean authenticated;

	public AuthTokenAuthentication(AuthUser authRestBean) {
		principal = authRestBean.getUser().getUserName();
		userDetail = authRestBean;
		authToken = authRestBean.getToken();
//		Set<String> perms = Optional.ofNullable(authRestBean.getPermissionSet()).orElse(new HashSet());
//		for (String perm : perms) {
//			authorityList.add(new SimpleGrantedAuthority(perm));
//		}
		if (null != userDetail.getUser().getRoleName() && !userDetail.getUser().getRoleName().isEmpty()) {
			authorityList.add(new SimpleGrantedAuthority(userDetail.getUser().getRoleName()));
		}
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorityList;
	}

	@Override
	public String getCredentials() {
		return authToken;
	}

	@Override
	public AuthUser getDetails() {
		return userDetail;
	}

	@Override
	public String getPrincipal() {
		return principal;
	}

	@Override
	public boolean isAuthenticated() {
		return authenticated;
	}

	@Override
	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
		authenticated = isAuthenticated;
	}

	@Override
	public String getName() {
		return principal;
	}
}
