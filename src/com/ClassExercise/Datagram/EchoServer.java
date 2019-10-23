package com.ClassExercise.Datagram;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class EchoServer {

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = null;
        try {
            ThreadPoolExecutor tpe = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
            serverSocket = new ServerSocket(6969);
            serverSocket.setReuseAddress(true);

            System.out.println("Server is up and running, waiting for connections...");

            while (true) {
                Socket client = serverSocket.accept();
                System.out.println("A new client has connected " + client.getInetAddress().getHostAddress());
                ClientHandler clientSocket = new ClientHandler(client);

                tpe.execute(clientSocket);
            }
        } catch (IOException ie) {
            System.out.println("An error has occured, please try again");
            ie.printStackTrace();
        }
    }
}
        class ClientHandler implements Runnable {

            private Socket clientSocket;

            public ClientHandler(Socket clientSocket) {
                this.clientSocket = clientSocket;
            }

            public void run() {
                PrintWriter output = null;
                BufferedReader input = null;
                try {
                    output = new PrintWriter(clientSocket.getOutputStream(), true);
                    input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    String line;
                    while ((line = input.readLine()) != null) {
                        System.out.println("Text sent from client: " + line);
                        output.println(line);
                    }
                } catch (IOException ie) {
                    System.out.println("A client has disconnected");
                    ie.printStackTrace();
                } finally {
                    try {
                        if (output != null) {
                            output.close();
                        }
                        if (input != null) {
                            input.close();
                            clientSocket.close();
                        }
                    } catch (IOException ie) {
                        ie.printStackTrace();
                    }
                }
            }
        }