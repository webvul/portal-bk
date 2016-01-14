package com.kii.beehive.portal.web.entity;


import org.junit.Test;

import com.kii.beehive.portal.web.WebTestTemplate;
import com.kii.beehive.portal.web.exception.PortalException;

import static junit.framework.TestCase.fail;


public class TestThingRestBean extends WebTestTemplate {

    private static final String KII_APP_ID = "5cdf9a64";

    @Test
    public void testVerifyVendorThingID() {

        String[] invalidVendoThingIDs = new String[] {
            ".qwe","qwe.","q.we", ".123", "123.", "1.23", "", null
        };

        for (String id : invalidVendoThingIDs) {
            ThingRestBean bean = new ThingRestBean();
            bean.setVendorThingID(id);
            bean.setKiiAppID(KII_APP_ID);

            try {
                bean.verifyInput();
                fail();
            }catch (PortalException e) {

            }
        }

        String[] vendoThingIDs = new String[] {
            "-qWe","Qwe-","q-wE", "-123", "123-", "1-23", "-Q1W2E3", "q1w2e3-", "q1-w2e3"
        };

        for (String id : vendoThingIDs) {
            ThingRestBean bean = new ThingRestBean();
            bean.setVendorThingID(id);
            bean.setKiiAppID(KII_APP_ID);

            try {
                bean.verifyInput();
            }catch (PortalException e) {
                System.out.println(e.getErrorMessage());
                e.printStackTrace();
                fail();
            }
        }

    }

}
