import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class threadedserver{

    private static final int PORT = 8080;
    private static final int MAX_ATTEMPTS = 5;
    private static final int RANDOM_RANGE = 10;

    public static void main(String[] args) {
        try {
            // Create a server socket
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server started on port " + PORT);

            while (true) {
                // Accept client connections
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                // Start a new thread to handle the client
                Thread clientThread = new Thread(new ClientHandler(clientSocket));
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private Random random;
        private int randomNumber;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
            this.random = new Random();
            this.randomNumber = random.nextInt(RANDOM_RANGE) + 1;
        }

        @Override
        public void run() {
            try {
                // Get input and output streams to communicate with the client
                InputStream input = clientSocket.getInputStream();
                OutputStream output = clientSocket.getOutputStream();

                boolean guessed = false;
                int attempts = 0;

                while (attempts < MAX_ATTEMPTS && !guessed) {
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
