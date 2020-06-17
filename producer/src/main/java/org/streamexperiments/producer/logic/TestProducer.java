package org.streamexperiments.producer.logic;

import org.streamexperiments.models.Update;
import org.streamexperiments.producer.integration.Sender;

import javax.annotation.PreDestroy;
import java.io.*;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Example of the producer's logic implementation decoupled from any integration matter.
 * Produces "N" instances of {@link Update} every "throughput" milliseconds and sends them
 * through each of the {@link Sender} stored the sender's set that it holds as attribute.
 *
 */
public class TestProducer {
    private static Logger logger = LogManager.getLogger(TestProducer.class);

    private final long waitTime = 10000;

    private long throughput;
    private long N;
    private String senderUUID;
    private Update update;
    private AtomicLong sequece = new AtomicLong();
    private Set<Sender> senders;
    private boolean started = false;

    /**
     * Initializes this producer with a (hopefully) unique identifier that will be inserted
     * in every {@link Update} produced. Also initializes the set of senders.
     *
     */
    public TestProducer(long throughput, long N, String senderUUID) {
        this.throughput = throughput;
        this.N = N;
        this.senderUUID = senderUUID;
        senders = new HashSet<>();

        if(new File(senderUUID).exists()) {
            try {
                Scanner scanner = new Scanner(new File(senderUUID));
                long offset = scanner.nextLong();
                sequece.set(offset);
            } catch (FileNotFoundException ex) {
                logger.error(ex.getMessage());
            }
        } else {
            sequece.set(0L);
        }
    }

    /**
     * Adds a {@link Sender} to be included in the set of senders.
     * @param sender Sender instance
     */
    public void addSender(Sender sender) {
        senders.add(sender);
    }

    /**
     * Starts the thread that will infinitely produce "N" messages each "throughput" milliseconds.
     */
    public void start() {
        if(started) {
            return;
        }

        System.out.println("**************************************************");
        System.out.println("**************************************************");
        System.out.println("********** @@@ TESTING PRODUCER @@@ ***********");
        System.out.println("* " + N + " updates each " + throughput + " milliseconds *");
        System.out.println("**************************************");
        System.out.println("**************************************************");
        System.out.println("******* Producer ID: " + senderUUID + " **********");
        System.out.println("******* Sequence: " + sequece.get() + " **********");
        System.out.println("**************************************************");

        new Thread(() -> {
            try {
                // Give time to integration layer to wake up:
                Thread.sleep(waitTime);
                while (true) {
                    Thread.sleep(throughput);

                    for(int i = 0; i < N; i++) {
                        update = new Update(sequece.getAndIncrement(), senderUUID, System.currentTimeMillis());
                        senders.forEach((sender) -> sender.send(update));
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        started = true;

        return;
    }

    public void shutdown() {
        System.out.println("**************************************************");
        System.out.println("**************************************************");
        System.out.println("********** @@@ FINISHING PRODUCER @@@ ************");
        System.out.println("**************************************************");
        System.out.println("**************************************************");

        try(FileWriter writer = new FileWriter(senderUUID)) {
            writer.write(String.valueOf(sequece.intValue()));
        } catch(IOException ex) {
            logger.error(ex.getMessage());
        }

    }
}
