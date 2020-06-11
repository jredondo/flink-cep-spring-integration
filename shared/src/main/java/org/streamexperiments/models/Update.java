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

    public void setId(long id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getSender() {
        return sender;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setSender(String sender) {
        this.sender = sender;
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
