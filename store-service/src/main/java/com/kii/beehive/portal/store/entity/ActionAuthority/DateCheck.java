package com.kii.beehive.portal.store.entity.ActionAuthority;

import java.util.Date;

public class DateCheck implements ActionCheck<Date> {

	private long lower;

	private long upper;

	@Override
	public boolean verify(Date value) {
		long time=value.getTime();

		return time>lower&&time<upper;
	}
}
