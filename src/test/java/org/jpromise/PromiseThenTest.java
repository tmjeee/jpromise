package org.jpromise;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertTrue;

public class PromiseThenTest {

    private static final Logger logger = LoggerFactory.getLogger(PromiseThenTest.class);

    volatile boolean executed = false;
    volatile boolean executed2 = false;

    @Before
    public void before() {
        executed = false;
        executed2 = false;
    }

    @Test
    public void test_then() throws Exception {
        logger.debug("start test_then");
        new Promise(res->{
            res.resolve("one");
        }).then(i->i+"two",
            e-> {
                fail("do not expect rejection");
        }).then(i->i+"three",
            e->{
                fail("do not expect rejection");
        }).then(i->i+"four",
            e->{
                fail("do not expect rejection");
        }).done((ResultCallback<String>) r->{
            assertEquals(r,"onetwothreefour");
            executed = true;
        }, e->{
            fail("do not expect rejection");
        });
        assertTrue(executed);
        logger.debug("done test_then");
    }

    @Test
    public void test_thenCatch() throws Exception {
        logger.debug("start test_thenCatch");
        boolean[] executions = new boolean[10];
        Exception e1 = new Exception("e1");
        new Promise(res->{
            res.reject(e1);
        })
        .thenCatch(e->{
            assertEquals(e, e1);
            executions[0] = true;
        })
        .thenTransform(r->"one")
        .thenTransform(r->r+"two")
        .done(r->{
            assertEquals(r, "onetwo");
            executions[1] = true;
        }, e->{
            fail("did not expect rejection");
        });
        assertTrue(executions[0]);
        assertTrue(executions[1]);
        logger.debug("done test_thenCatch");
    }


    @Test
    public void test_thenCatch2() throws Exception {
        logger.debug("start test_thenCatch2");
        RuntimeException e1 = new RuntimeException("e1");
        new Promise(res->{
            res.resolve("one");
        })
        .thenCatch(e->{
            fail("do not expect thenCatch");
        })
        .thenTransform(r->{throw e1;})
        .thenTransform(r->r+"two")
        .thenCatch(e->{
            assertEquals(e, e1);
            executed = true;
        })
        .done(r->{
             assertEquals(r, "one");
             executed2 = true;
        }, e->{
             fail("did not expect rejection");
        });
        assertTrue(executed);
        assertTrue(executed2);
        logger.debug("done test_thenCatch2");
    }

    @Test
    public void test_thenCatch3() throws Exception {
        logger.debug("start test_thenCatch3");
        RuntimeException e1 = new RuntimeException("e1");
        new Promise(res->{
            res.resolve("one");
        })
            .thenCatch(e->{
                fail("do not expect thenCatch");
            })
            .thenTransform(r->{
                return r+"two";
            })
            .thenTransform(r->{
                throw e1;
            })
            .thenCatch(e->{
                assertEquals(e, e1);
                executed = true;
            })
            .done(r->{
                assertEquals(r, "onetwo");
                executed2 = true;
            }, e->{
                fail("did not expect rejection");
            });
        assertTrue(executed);
        assertTrue(executed2);
        logger.debug("done test_thenCatch3");
    }

    @Test
    public void test_thenTransform() throws Exception {
        logger.debug("start test_thenTransform");
        new Promise(res->{
            res.resolve("one");
        }).thenTransform(i->i+"two"
            ).thenTransform(i->i+"three"
            ).thenTransform(i->i+"four"
            ).done((ResultCallback<String>) r->{
                    assertEquals(r,"onetwothreefour");
                    executed = true;
                }, e->{
                    fail("do not expect rejection");
        });
        assertTrue(executed);
        logger.debug("done test_thenTransform");
    }
}
