package org.streamexperiments.models;

/**
 * POJO used as canonical data model.
 *
 */
public class Update {

    private long id;
    private long timestamp;
    private String sender;

    public Update() {
    }

    public Update(long id, String sender, long timestamp) {
        this.id = id;
        this.timestamp = timestamp;
        this.sender = sender;
    }

    public long getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getSender() {
        return sender;
    }

    @Override
    public String toString() {
        return "Update{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", sender='" + sender + '\'' +
                '}';
    }
}
