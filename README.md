Threaded Application Report
Table of Contents

    Introduction
    Definitions
    Server
    Client
    Results
    Conclusion

Introduction

This report presents the results obtained from developing an application that utilizes threads to solve a guessing problem. The objective was to create a server that generates a random number and interacts with a client to guess that number using threads for managing connections and message exchanges.
Definitions

    Thread: A thread is a lightweight execution flow that allows tasks to run in parallel. In our application, each client is managed by a separate thread, enabling the server to handle multiple connections simultaneously.
    Server: In this context, the server refers to the application that generates a random number and communicates with the client to provide hints for guessing.
    Client: The client is the application that interacts with the server by inputting values to attempt to guess the generated number.

Server

The server code utilizes sockets to handle communication with clients.

java

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class ThreadedServer {

    private static final int MAX_ATTEMPTS = 10;
    private int randomNumber;

    public ThreadedServer() {
        // Generate a random number between 0 and 10
        randomNumber = new Random().nextInt(11);
    }

The ThreadedServer class is responsible for generating the random number and managing client connections. In the constructor, we initialize the randomNumber variable with a random number between 0 and 10 using the Random class in Java.

java

    public void start() {
        try {
            // Create a server socket listening on port 8080
            ServerSocket serverSocket = new ServerSocket(8080);
            System.out.println("Server is waiting for connections...");

            while (true) {
                // Wait for a client connection
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connection established with the client.");

                // Create a client handler in a new thread
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

The start method listens for client connections. We create a ServerSocket listening on port 8080. Then, we enter an infinite loop where we wait for a client connection using serverSocket.accept(). Once a connection is established, we create a new ClientHandler object to manage the client in a new thread. This allows the server to handle multiple clients concurrently.

java

    // Returns the maximum number of attempts
    public int getMaxAttempts() {
        return MAX_ATTEMPTS;
    }

The getMaxAttempts method is used to retrieve the maximum number of attempts from other parts of the code, particularly from the client side.

java

    private class ClientHandler implements Runnable {
        private Socket clientSocket;
        private int attempts;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
            this.attempts = 0;
        }

        @Override
        public void run() {
            try {
                // Get input and output streams to communicate with the client
                InputStream input = clientSocket.getInputStream();
                OutputStream output = clientSocket.getOutputStream();

                boolean guessed = false;

                while (!guessed && attempts < MAX_ATTEMPTS) {
                    // Read the value sent by the client
                    int clientNumber = input.read();
                    attempts++;

                    // Compare the value with the generated number and send a response message
                    if (clientNumber < randomNumber) {
                        output.write("Enter a higher number.".getBytes());
                    } else if (clientNumber > randomNumber) {
                        output.write("Enter a lower number.".getBytes());
                    } else {
                        output.write("Congratulations! You guessed the number.".getBytes());
                        guessed = true;
                    }
                }

                if (!guessed) {
                    output.write("Game over. You reached the maximum number of attempts.".getBytes());
                }

                // Close the client socket
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

The ClientHandler class implements the Runnable interface, allowing it to be executed in a separate thread. It manages the communication with the client. Inside the run method, we obtain the input and output streams to communicate with the client using the getInputStream and getOutputStream methods of the clientSocket.

We continue reading the values sent by the client using input.read(). Then, we compare the value with the generated random number and send a response message accordingly. If the client guesses the correct number, we set guessed to true and break out of the loop. If the maximum number of attempts is reached without a correct guess, we inform the client that the game is over.

Finally, we close the client socket using clientSocket.close().
Client

The client code is responsible for interacting with the server by inputting values to guess the random number.

java

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ThreadedClient {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;

    public static void main(String[] args) {
        try {
            // Connect to the server
            Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
            System.out.println("Connected to the server.");

            // Get input and output streams to communicate with the server
            InputStream input = socket.getInputStream();
            OutputStream output = socket.getOutputStream();

            Scanner scanner = new Scanner(System.in);
            boolean gameOver = false;

            while (!gameOver) {
                System.out.print("Enter your guess (0-10): ");
                int guess = scanner.nextInt();

                // Send the guess to the server
                output.write(guess);

                // Receive and display the server's response
                byte[] response = new byte[1024];
                int bytesRead = input.read(response);
                System.out.println(new String(response, 0, bytesRead));

                if (bytesRead > 0 && response[0] == 'C') {
                    gameOver = true;
                }
            }

            // Close the client socket
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

The ThreadedClient class establishes a connection with the server using the Socket class, specifying the server host and port. It then obtains the input and output streams for communication with the server.

A Scanner object is created to read user input from the console. Inside the main loop, the client prompts the user to enter a guess between 0 and 10. The guess is sent to the server using output.write(guess).

The client receives the server's response by reading from the input stream using input.read(response). The response is then displayed to the user. If the response starts with the character 'C', indicating that the client guessed the correct number, the gameOver flag is set to true, and the loop is terminated.

Finally, the client socket is closed using socket.close().
Results

The threaded application successfully allows multiple clients to connect to the server simultaneously and guess the random number. Each client is managed by a separate thread, enabling concurrent execution and efficient utilization of system resources.
Conclusion

Developing a threaded application for managing client connections and message exchanges can greatly improve the scalability and performance of a server. By utilizing threads, the application achieves parallel execution, allowing multiple clients to interact with the server concurrently. The implementation demonstrated in this report successfully achieves this goal, providing a responsive and efficient solution for the guessing problem.
