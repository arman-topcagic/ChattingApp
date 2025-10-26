import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12346;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String username;

    public Client() {
        connectToServer();
        startChat();
    }

    private void connectToServer() {
        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter your username: ");
            username = scanner.nextLine().trim();
            if (username.isEmpty()) username = "Anonymous";

            // Send username to server
            out.println(username);

            // Thread to listen for incoming messages
            new Thread(() -> {
                try {
                    String line;
                    while ((line = in.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException e) {
                    System.out.println("Connection lost.");
                }
            }).start();

        } catch (IOException e) {
            System.out.println("Unable to connect to server.");
            e.printStackTrace();
            System.exit(0);
        }
    }

    private void startChat() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String message = scanner.nextLine();
            if (!message.trim().isEmpty()) {
                out.println(message);
            }
        }
    }

    public static void main(String[] args) {
        new Client();
    }
}
