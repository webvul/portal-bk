package com.kii.beehive.portal.store.entity;

import java.util.Map;
import java.util.Set;

public enum LandLordPermit {


	NONE(0),STATUS(1),ACTION(2),ALL(3);

	private int mask;

	LandLordPermit(int mask){
		this.mask=mask;
	}

	public LandLordPermit  or(LandLordPermit permit){


		int i=mask|permit.mask;

		return  LandLordPermit.values()[i];

	}


}
