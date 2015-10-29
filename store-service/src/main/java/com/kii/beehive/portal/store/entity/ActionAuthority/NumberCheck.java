package com.kii.beehive.portal.store.entity.ActionAuthority;

import java.util.function.Function;

public class NumberCheck implements ActionCheck<Number> {




	private Function<Number,Boolean> verifyFun;

	@Override
	public boolean verify(Number value) {

		return verifyFun.apply(value);
	}

}
