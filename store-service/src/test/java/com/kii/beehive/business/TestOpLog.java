package com.kii.beehive.business;


import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.StoreServiceTestInit;
import com.kii.beehive.business.helper.OpLogTools;

public class TestOpLog extends StoreServiceTestInit {

	@Autowired
	private OpLogTools  opLogTools;

	@Test
	public void testSwitch(){



		opLogTools.write(new String[]{"foo","bar"});

		opLogTools.doDailySwitch();

		opLogTools.write(new String[]{"abc","xyz"});
	}

}
