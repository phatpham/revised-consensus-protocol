package com.consensus;

import java.io.*;
import java.net.Socket;

public class Participant {
    private int pport;
    private int cport;
    private String url;
    private int timeout;

    public Participant(int pport, int cport, String url, int timeout) {
        this.pport = pport;
        this.cport = cport;
        this.url = url;
        this.timeout = timeout;
    }

    public static void main(String[] args) {
        Participant participant1 = new Participant(200, 4444, "127.0.0.1", 500);
        Participant participant2 = new Participant(201, 4444, "127.0.0.1", 500);
        Thread thread1 = new Thread(new ParticipantToCoord(participant1));
        thread1.start();
        Thread thread2 = new Thread(new ParticipantToCoord(participant2));
        thread2.start();
    }

    public String getUrl() {
        return url;
    }

    public int getPport() {
        return pport;
    }

    public int getCport() {
        return cport;
    }

    public int getTimeout() {
        return timeout;
    }
}
