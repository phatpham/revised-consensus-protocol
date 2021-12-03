package com.consensus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;


public class CoordinatorHandler implements Runnable{
    private Coordinator coor;

    public CoordinatorHandler(Coordinator coor) {
        this.coor = coor;
    }

    public void run() {
        try {
            this.listen();
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public void listen() throws IOException {
        ServerSocket socket = new ServerSocket(coor.getCport());
        for (; ; ) {
            Socket client = socket.accept();
            //Let all parts join
            answer(client);

            //Create new thread for each socket
            if (coor.getPariticipantsMap().size() == coor.getNumOfParts()) {
                System.out.println("ALL CONNECTED");
                for (Map.Entry<Integer, Socket> participant : coor.getPariticipantsMap().entrySet()) {
                    CoordinatorListener listener = new CoordinatorListener(coor.getCport(), participant.getKey(),
                            participant.getValue(), coor.getPariticipantsMap().keySet(), coor.getVoteOptions());
                    Thread coorListener = new Thread(listener);
                    coorListener.start();
                }
            }
        }
    }


    public boolean answer(Socket client) {
        BufferedReader input;
        try {
            input = new BufferedReader(new InputStreamReader(client.getInputStream()));
            while (true) {
                String line  = input.readLine();
                String[] split = line.split(" ");
                coor.getPariticipantsMap().put(Integer.parseInt(split[1]), client);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
