package com.example.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class ChatServer {
    public static void main(String[] args) throws Exception {
        System.out.println("The chat server is running...");
        ExecutorService pool = Executors.newFixedThreadPool(500);
        try (ServerSocket listener = new ServerSocket(7005)) {
            while (true) {
                Socket socket = listener.accept();
                pool.execute(new ServerHandler(socket));
            }
        }
    }
}
