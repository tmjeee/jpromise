package org.jpromise;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertTrue;

public class PromiseRejectTest {

    private static final Logger logger = LoggerFactory.getLogger(PromiseRejectTest.class);

    private volatile boolean executed = false;

    @Before
    public void before() {
        executed = false;
    }

    @Test
    public void test_reject() throws Exception {
        logger.debug("starting test_reject");
        final Exception exception =new Exception("test");

        Promise.reject(exception)
            .done(r->{
                fail("do not expect a resolved result");
            }, e->{
                assertEquals(exception, e);
                executed = true;
            });

        assertTrue(executed);
        logger.debug("end test_reject");
    }
}
