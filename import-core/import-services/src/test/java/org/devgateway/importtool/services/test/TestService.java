package org.devgateway.importtool.services.test;

import org.devgateway.importtool.services.ServiceConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestService.TestServiceConfiguration.class)
@Transactional
@TransactionConfiguration
/***
 * Sample test
 * @author Fernando
 *
 */
public class TestService {


    @Configuration
    @EnableAutoConfiguration
    @Import(ServiceConfiguration.class)
    static class TestServiceConfiguration {
    }

    @Before
    public void begin() throws Throwable {
    }

    @Test
    public void testCreateUser() throws Throwable {
    }
}
