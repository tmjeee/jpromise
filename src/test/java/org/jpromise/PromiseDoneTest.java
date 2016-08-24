package org.jpromise;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

public class PromiseDoneTest {

    private static final Logger logger = LoggerFactory.getLogger(PromiseDoneTest.class);

    volatile boolean executed = false;

    @Before
    public void before() {
        executed = false;
    }

    @Test
    public void test_resolve() throws Exception {
        logger.debug("Start test_resolve");
        new Promise(res->{
            res.resolve("test");
        }).done(r->{
            assertEquals(r, "test");
            executed = true;
        }, e->{
            fail("do not expect rejection");
        });
        assertTrue(executed);
        logger.debug("Done test_resolve");
    }


    @Test
    public void test_reject() throws Exception {
        logger.debug("Start test_reject");
        final Exception _e = new Exception("test");
        new Promise(res->{
            res.reject(_e);
        }).done(r->{
            fail("do not expect resolution");
        }, e->{
            assertEquals(e, _e);
            executed = true;
        });
        assertTrue(executed);
        logger.debug("Done test_reject");
    }


    @Test
    public void test_doneResolve() throws Exception {
        logger.debug("Start doneResolve");
        new Promise(res->{
            res.resolve("test");
        }).doneResolve(r->{
            assertEquals(r, "test");
            executed = true;
        });
        assertTrue(executed);
        logger.debug("Done doneResolve");
    }


    @Test
    public void test_doneReject() throws Exception {
        logger.debug("Start test_doneReject");
        final Exception _e = new Exception("test");
        new Promise(res->{
            res.reject(_e);
        }).doneReject(e->{
            assertEquals(e, _e);
            executed = true;
        });
        assertTrue(executed);
        logger.debug("Done test_doneReject");
    }
}
