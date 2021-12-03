package com.consensus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.List;
import java.util.Set;

public class CoordinatorListener implements Runnable{
    private int cport;
    private int pport;
    private Set<Integer> participants;
    private Set<String> voteOptions;
    BufferedReader input;
    PrintStream out;
    public CoordinatorListener(int cport, int pport, Socket client, Set<Integer> participants, Set<String> voteOptions) throws IOException {
        this.cport = cport;
        this.pport = pport;
        this.participants = participants;
        this.voteOptions = voteOptions;
        input = new BufferedReader(new InputStreamReader(client.getInputStream()));
        out = new PrintStream(client.getOutputStream());
    }

    @Override
    public void run() {
        sendDetails();
        sendVoteOptions();
    }

    public void sendDetails() {
        out.println("DETAILS " + participants.toString());
        out.flush();
    }

    public void sendVoteOptions() {
        out.println("VOTE_OPTIONS " + voteOptions);
        System.out.println(22);
        out.flush();
    }
}
