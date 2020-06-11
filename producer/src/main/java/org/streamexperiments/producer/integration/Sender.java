package org.streamexperiments.producer.integration;

import org.streamexperiments.models.Update;

/**
 * Condenses gateway and connection information for every integration mechanism.
 * It is the facade used by the producer's logic to abstract communication.
 *
 */
public class Sender {
    private Gateway gateway;
    private String connectionId;

    /**
     * Constructor.
     *
     * @param gateway
     * @param connectionId
     */
    public Sender(Gateway gateway, String connectionId) {
        this.gateway = gateway;
        this.connectionId = connectionId;
    }

    /**
     * Convenient method for sending {@link Update}
     *
     * @param update
     */
    public void send(Update update) {
        gateway.send(update, connectionId);
    }
}
