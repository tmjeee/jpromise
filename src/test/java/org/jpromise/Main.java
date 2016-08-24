package org.jpromise;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        new Promise(resolution->{
            Thread t = new Thread(()->{
                try {
                    logger.info("going to sleep");
                    Thread.sleep(20);
                    logger.info("Done sleeping");
                    resolution.resolve("MyResult");
                } catch (InterruptedException e) {
                    logger.error(e.toString(), e);
                }
            });
            t.setName("testing-run");
            t.start();
        }).then(
            result->{
                logger.info("Result = "+result);
                return "MyResult2";
            },
            error->{
                logger.info("Error = "+error);
            })
        .done(
            result->{
                logger.info("Result2 = "+result);
            },
            error->{
                logger.info("Error2 = "+error);
            }
        );
    }
}
