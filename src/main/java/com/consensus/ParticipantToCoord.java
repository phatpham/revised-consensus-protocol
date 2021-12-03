package com.consensus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class ParticipantToCoord implements Runnable{
    private Participant participant;
    private Set<Integer> otherParticipants;
    private List<String> voteOptions;
    Socket csocket;
    List<Socket> receivingSockets;
    List<Socket> sendingSockets;
    private Map<Integer, String> result;
    public ParticipantToCoord(Participant participant) {
        this.participant = participant;
        otherParticipants = new HashSet<>();
        voteOptions = new ArrayList<>();
        sendingSockets = new ArrayList<>();
        receivingSockets = new ArrayList<>();
        result = new HashMap<>();
    }
    @Override
    public void run() {
        try {
            new Thread(this::startListening).start();
            csocket = establishConnection();
            sendInitialMessage(csocket);
            listenToCoordinator(csocket);
            Thread.sleep(1000);
            establishConnectionToParticipants();
            startConsensusProtocol();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void startConsensusProtocol() throws InterruptedException, IOException {
        int round = 1;
        String vote = decideVote();
        Map<Integer, String> voteThisRound = new HashMap<>();
        voteThisRound.put(participant.getPport(), vote);
        while (round <= otherParticipants.size() && !voteThisRound.isEmpty()) {
            String message = formatMessageToPeer(voteThisRound);
            for (Socket peer : receivingSockets) {
                sendMessageToPeer(message);
            }
            Thread.sleep(participant.getTimeout());
            while () {
                for (Socket peer : receivingSockets) {
                    BufferedReader input = new BufferedReader(new InputStreamReader(peer.getInputStream()));
                    String line = input.readLine();

                }
            }
            round++;
        }
    }

    public void startListening(){
        try {
            ServerSocket listenSocket = new ServerSocket(participant.getPport());
            while (true) {
                Socket other = listenSocket.accept();
                receivingSockets.add(other);
                if (receivingSockets.size() == otherParticipants.size()){
                    System.out.println("ALL p2p connected");
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void establishConnectionToParticipants() throws IOException {
        for (int part : otherParticipants) {
            Socket client = new Socket("127.0.0.1", part);
            sendingSockets.add(client);
        }
    }

    public String decideVote() {
        Random random = new Random();
        return voteOptions.get(random.nextInt(voteOptions.size()));
    }
    public Socket establishConnection() throws IOException {
        return new Socket(participant.getUrl(), participant.getCport());
    }

    public void sendInitialMessage(Socket socket) {
        PrintStream out;
        try {
            out = new PrintStream(socket.getOutputStream());
            out.println("JOIN " + participant.getPport());
            System.out.println("send from " + participant.getPport());
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void listenToCoordinator(Socket socket) throws IOException {
        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        while (true) {
            if (parseCommands(input.readLine()))
                break;
        }
    }

    public boolean parseCommands(String command) {
        String[] split = command.split(" ",2);
        if (split[0].equals("DETAILS")) {
            String second = split[1].replaceAll("[^a-zA-Z0-9,]", "");
            second = second.trim();
            for (String part : second.split(",")) {
                int intPart = Integer.parseInt(part);
                if (intPart != participant.getPport()) {
                    otherParticipants.add(intPart);
                }
            }
        } else if (split[0].equals("VOTE_OPTIONS")) {
            String second = split[1].replaceAll("[^a-zA-Z0-9,]", "");
            second = second.trim();
            voteOptions.addAll(Arrays.asList(second.split(",")));
            return true;
        }
        return false;
    }

}
