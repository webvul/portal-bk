package com.kii.beehive.portal.jdbc;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:com/kii/beehive/portal/jdbc/portalJdbcContext.xml" })
@Transactional
@Rollback
public class TestTemplate  {

    @Test
    public void test() {
        // empty method for junit error
    }



}
