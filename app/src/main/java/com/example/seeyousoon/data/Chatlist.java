package com.example.seeyousoon.data;

import java.util.Date;

public class Chatlist{
    public String id;
    long timestamp;

    public Chatlist(String id) {
        this.id = id;
    }

    public Chatlist() {
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}