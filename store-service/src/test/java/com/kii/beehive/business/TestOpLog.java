package com.kii.beehive.business;


import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.business.helper.OpLogTools;
import com.kii.beehive.portal.store.*;

public class TestOpLog extends com.kii.beehive.portal.store.TestInit {

	@Autowired
	private OpLogTools  opLogTools;

	@Test
	public void testSwitch(){



		opLogTools.write(new String[]{"foo","bar"});

		opLogTools.doDailySwitch();

		opLogTools.write(new String[]{"abc","xyz"});
	}

}
