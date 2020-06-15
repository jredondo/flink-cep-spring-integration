package org.streamexperiments.producer.logic;

import org.streamexperiments.models.Update;
import org.streamexperiments.producer.integration.Sender;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Example of the producer's logic implementation decoupled from any integration matter.
 * Produces "N" instances of {@link Update} every "throughput" milliseconds and sends them
 * through each of the {@link Sender} stored the sender's set that it holds as attribute.
 *
 */
public class TestProducer {

    private final long waitTime = 10000;

    private long throughput;
    private long N;
    private final String senderHashCode;
    private Update update;
    private AtomicLong sequece = new AtomicLong();
    private Set<Sender> senders;
    private boolean started = false;

    /**
     * Initializes this producer with a (hopefully) unique identifier that will be inserted
     * in every {@link Update} produced. Also initializes the set of senders.
     *
     */
    public TestProducer(long throughput, long N) {
        this.throughput = throughput;
        this.N = N;
        senderHashCode = Integer.toString((int)(Math.random()*Integer.MAX_VALUE));
        senders = new HashSet<>();
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

        System.out.println("********** @@@ TESTING PRODUCER @@@ ***********");
        System.out.println("* " + N + " updates each " + throughput + " milliseconds *");
        System.out.println("**************************************");
        System.out.println("**************************************************");
        System.out.println("******* Producer ID: " + senderHashCode + " **********");
        System.out.println("**************************************************");

        new Thread(() -> {
            try {
                // Give time to integration layer to wake up:
                Thread.sleep(waitTime);
                while (true) {
                    Thread.sleep(throughput);

                    for(int i = 0; i < N; i++) {
                        update = new Update(sequece.getAndIncrement(), senderHashCode, System.currentTimeMillis());
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
}
