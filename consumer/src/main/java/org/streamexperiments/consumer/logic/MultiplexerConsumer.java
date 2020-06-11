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

    private Map<String, Set<Update>> map = new HashMap<>();

    public void consume(Update update) {
        Set<Update> set = map.computeIfAbsent(update.getSender(), (key) -> new HashSet<Update>());
        if(set.size() % Consumer.PRINT_COUNT == 0) {
            logger.info(set.size() + " items for sender: " + update.getSender());
        }
        set.add(update);
    }
}
