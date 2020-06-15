package org.streamexperiments.consumer.logic;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.streamexperiments.models.Update;

import java.util.*;

/**
 * Still very simple {@link Consumer} implementation.
 * Keep an index by sender of message arrived.
 *
 * @author Jorge Redondo Flames <jorge.redondo@gmail.com>
 *
 */
public class MultiplexerConsumer implements Consumer {

    private final long interval = 5000;

    private static Logger logger = LogManager.getLogger(MultiplexerConsumer.class);

    private Map<String, Record> map = new HashMap<>();

    public void consume(Update update) {
        Record record = map.computeIfAbsent(update.getSender(),
                (key) -> new Record(0L, System.currentTimeMillis(), update.getId()));

        if(record.count % Consumer.PRINT_COUNT == 0) {
            senderLogReport(update, record);
            verifyDataLoss(record);
        }

        record.update(update);
    }

    private void verifyDataLoss(Record record) {
        List<Long> list = new ArrayList<>(record.set);
        Collections.sort(list);
        long aux = -1;
        for(Long item: list) {
            if(aux == -1) {
                aux = item;
                continue;
            } else {
                if(item - aux != 1) {
                    logger.info("###############################");
                    logger.info("###############################");
                    logger.info("########## DATA LOSS ##########");
                    logger.info("########## Expected: " + (aux + 1));
                    logger.info("########## Got: " + item);
                    logger.info("###############################");
                    logger.info("###############################");
                }
                aux = item;
            }
        }
    }

    private void senderLogReport(Update update, Record record) {
        logger.info("SENDER: " + update.getSender());
        logger.info(record.count + " items.");
        logger.info(Consumer.PRINT_COUNT + " items in (ms): " + (System.currentTimeMillis() - record.lastTime));
        logger.info("Registered producers: " + map.size() + "\n\n");
        record.lastTime = System.currentTimeMillis();

    }

    private class Record {
        Long count;
        Long lastTime;
        Long lastId;
        Set<Long> set = new HashSet<>();

        public Record(Long count, Long time, Long lastId) {
            this.count = count;
            this.lastTime = time;
            this.lastId = lastId;
        }

        public void update(Update update) {
            count++;
            lastId = update.getId();
            set.add(update.getId());
        }

    }
}
