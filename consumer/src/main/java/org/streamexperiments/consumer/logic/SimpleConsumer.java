package org.streamexperiments.consumer.logic;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.streamexperiments.models.Update;


/**
 * Very simple {@link Consumer} implementation: simply count messages.
 *
 * @author Jorge Redondo Flames <jorge.redondo@gmail.com>
 *
 */
public class SimpleConsumer implements Consumer {

    private final long THRESHOLD = 10000;
    private static Logger logger = LogManager.getLogger(SimpleConsumer.class);

    private long count;

    public void consume(Update update) {
        // Insert here whatever processing to Update instances you may need:
        // (As for now, only print the count)
        if(count % THRESHOLD == 0) {
            logger.info(count + " items processed");
        }
        count++;

    }
}

