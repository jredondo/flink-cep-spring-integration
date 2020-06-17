package org.streamexperiments.consumer.logic;

import org.streamexperiments.models.Update;

/**
 * The simplest consumer contract convenient to abstract consumer's logic applied to any {@link Update} object,
 * no matter how it came in
 *
 * @author Jorge Redondo Flames <jorge.redondo@gmail.com>.
 */
public interface Consumer {
    void consume(Update update);
}
