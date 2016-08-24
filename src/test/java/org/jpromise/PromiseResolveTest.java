package org.jpromise;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.String.format;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertTrue;

public class PromiseResolveTest {

    private static final Logger logger = LoggerFactory.getLogger(PromiseResolveTest.class);

    private volatile boolean executed = false;

    @Before
    public void before() {
        executed = false;
    }

    @Test
    public void test_resolve() {
        logger.debug(format("start test_resolve"));
        Promise.resolve("test")
            .done(r->{
                logger.debug(format("resolved as %s", r));
                assertEquals("test",r);
                executed = true;
            }, e->{
                logger.error(format("error %s was not expected", e));
                fail();
            });
        assertTrue(executed);
        logger.debug("done test_resolve");
    }
}
