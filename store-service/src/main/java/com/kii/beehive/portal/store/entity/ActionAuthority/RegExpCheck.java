package com.kii.beehive.portal.store.entity.ActionAuthority;

import java.util.regex.Pattern;

public class RegExpCheck implements ActionCheck<String> {

	private Pattern pattern;

	@Override
	public boolean verify(String value) {
		return pattern.matcher(value).matches();
	}
}
