package org.streamexperiments.consumer.logic;

import org.junit.Test;
import org.streamexperiments.models.Update;

import static org.junit.Assert.*;

public class IntegrityVerifierConsumerTest {

    @Test
    public void dataLossFalse() {
        IntegrityVerifierConsumer verifierConsumer = new IntegrityVerifierConsumer();
        String sender = "sender1";

        Update update = new Update(1000, sender, 0);

        verifierConsumer.consume(update);

        update = new Update(1001, sender, 1);

        verifierConsumer.consume(update);

        update = new Update(1002, sender, 2);

        verifierConsumer.consume(update);

        assertFalse(verifierConsumer.dataLoss(sender));

    }

    @Test
    public void dataLossTrue() {
        IntegrityVerifierConsumer verifierConsumer = new IntegrityVerifierConsumer();
        String sender = "sender1";

        Update update = new Update(1000, sender, 0);

        verifierConsumer.consume(update);

        update = new Update(1001, sender, 1);

        verifierConsumer.consume(update);

        update = new Update(1003, sender, 2);

        verifierConsumer.consume(update);

        assertTrue(verifierConsumer.dataLoss(sender));

    }

}