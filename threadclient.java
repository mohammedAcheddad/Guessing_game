import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class threadedclient {

    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 8080;

    public static void main(String[] args) {
        try {
            // Connect to the server
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            System.out.println("Connected to the server.");

            // Get input and output streams for communication
            InputStream input = socket.getInputStream();
            OutputStream output = socket.getOutputStream();

            Scanner scanner = new Scanner(System.in);
            boolean gameFinished = false;

            while (!gameFinished) {
                // Prompt the user to enter a number
                System.out.print("Enter a number (1-10): ");
                int number = scanner.nextInt();

                // Send the number to the server
                output.write(number);

                // Read the response from the server
                byte[] buffer = new byte[1024];
                int bytesRead = input.read(buffer);
                String response = new String(buffer, 0, bytesRead);

                // Display the server's response
                System.out.println("Server: " + response);

                // Check if the game is finished
                if (response.contains("Congratulations") || response.contains("Game over")) {
                    gameFinished = true;
                }
            }

            // Close the socket
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
