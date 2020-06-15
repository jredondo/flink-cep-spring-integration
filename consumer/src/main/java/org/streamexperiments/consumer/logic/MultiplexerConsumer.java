package org.streamexperiments.consumer.logic;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.streamexperiments.models.Update;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Still very simple {@link Consumer} implementation.
 * Keep an index by sender of message arrived.
 *
 * @author Jorge Redondo Flames <jorge.redondo@gmail.com>
 *
 */
public class MultiplexerConsumer implements Consumer {

    private static Logger logger = LogManager.getLogger(MultiplexerConsumer.class);

    private Map<String, Long> map = new HashMap<>();

    public void consume(Update update) {
        Long count = map.computeIfAbsent(update.getSender(), (key) -> 1L);
        if(count % Consumer.PRINT_COUNT == 0) {
            logger.info(count + " items for sender: " + update.getSender());
            logger.info("Registered producers: " + map.size());
        }
        map.put(update.getSender(), ++count);
    }
}
