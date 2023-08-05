package com.overload;

public class Message {
    private final String type;
    private int destinationId;

    public Message(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public int getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(int destinationId) {
        this.destinationId = destinationId;
    }
}
