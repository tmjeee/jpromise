package org.jpromise;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertTrue;

public class PromiseAllTest {

    private static final Logger logger = LoggerFactory.getLogger(PromiseAllTest.class);

    private volatile boolean executed = false;

    @Before
    public void before() {
        executed = false;
    }

    @Test
    public void test_all() {
        logger.debug("start test_all");
        Promise.all(
            res ->{
               res.resolve("one");
            },
            res ->{
               res.resolve("two");
            },
            res ->{
                res.resolve("three");
            }
        ).done(o->{
            assertTrue(o instanceof Object[]);
            assertEquals(((Object[])o)[0], "one");
            assertEquals(((Object[])o)[1], "two");
            assertEquals(((Object[])o)[2], "three");
            executed = true;
        }, e->{
            fail("not expecting rejection");
        });
        assertTrue(executed);
        logger.debug("end test_all");
    }

    @Test
    public void test_all2() throws Exception {
        logger.debug("start test_all");
        Exception exception = new Exception("test");
        Promise.all(
            res ->{
                res.resolve("one");
            },
            res ->{
                res.reject(exception);
            },
            res ->{
                res.resolve("three");
            }
        ).done(o->{
            assertTrue(o instanceof Object[]);
            assertEquals(((Object[])o)[0], "one");
            assertEquals(((Object[])o)[1], exception);
            assertEquals(((Object[])o)[2], "three");
            executed = true;
        }, e->{
            fail("not expecting rejection");
        });
        assertTrue(executed);
        logger.debug("end test_all");
    }
}
