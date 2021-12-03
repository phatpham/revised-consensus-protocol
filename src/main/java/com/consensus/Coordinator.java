package com.consensus;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLOutput;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Coordinator {
    private int count;
    private final int cport;
    private final int numOfParts;
    private final Set<String> voteOptions;
    private final Map<Integer, Socket> pariticipantsMap;

    public Coordinator(int cport, int numOfParts, Set<String> voteOptions){
        this.count = 0;
        this.cport = cport;
        this.numOfParts = numOfParts;
        pariticipantsMap = new HashMap<>();
        this.voteOptions = voteOptions;
    }
    public static void main(String[] args) {
        Set<String> voteOptions = new HashSet<>();
        voteOptions.add("A");
        voteOptions.add("B");
        Coordinator coordinator = new Coordinator(4444, 2, voteOptions);
        Thread coor = new Thread(new CoordinatorHandler(coordinator));
        coor.start();
    }

    public int getCount() {
        return count;
    }

    public void addToCount(){
        count++;
    }

    public int getCport() {
        return cport;
    }

    public int getNumOfParts() {
        return numOfParts;
    }

    public Map<Integer, Socket> getPariticipantsMap() {
        return pariticipantsMap;
    }

    public Set<String> getVoteOptions(){
        return voteOptions;
    }
}
