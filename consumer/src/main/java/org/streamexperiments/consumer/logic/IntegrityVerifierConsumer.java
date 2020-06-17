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
public class IntegrityVerifierConsumer implements Consumer {
    private final long THRESHOLD = 5;

    private static Logger logger = LogManager.getLogger(IntegrityVerifierConsumer.class);

    private Map<String, ProducerRecord> map = new HashMap<>();

    public void consume(Update update) {
        ProducerRecord record = map.computeIfAbsent(update.getSender(),
                (key) -> new ProducerRecord());

        record.update(update);

        if(record.ids.size() % THRESHOLD == 0) {
            senderLogReport(THRESHOLD, update.getSender(), record);
            if(dataLoss(update.getSender())) {
                printDataLossBanner();
            }
        }

    }

    public boolean dataLoss(String sender) {
        ProducerRecord record = map.get(sender);
        return record != null && record.dataLoss();
    }

    private void printDataLossBanner() {
        logger.info("###############################");
        logger.info("###############################");
        logger.info("########## DATA LOSS ##########");
        logger.info("###############################");
        logger.info("###############################");
    }

    private void senderLogReport(long numberOfItems, String sender, ProducerRecord record) {
        logger.info("SENDER: " + sender);
        logger.info(record.ids.size() + " items.");
        logger.info(numberOfItems + " items in: " + (System.currentTimeMillis() - record.lastTime) + "ms");
        logger.info("Registered producers: " + map.size() + "\n\n");
    }

    private class ProducerRecord {
        Long lastTime;
        List<Long> ids = new ArrayList<>();

        public void update(Update update) {
            ids.add(update.getId());
            lastTime = System.currentTimeMillis();
        }

        private boolean dataLoss() {
            List<Long> copy = new ArrayList<>(ids);
            Collections.sort(copy);
            long aux = -1;
            for (Long item : copy) {
                if (aux == -1) {
                    aux = item;
                    continue;
                } else {
                    if (item - aux != 1) {
                        return true;
                    }
                    aux = item;
                }
            }
            return false;
        }

    }
}
