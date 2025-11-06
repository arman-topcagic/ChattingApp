import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Server {
    private static final int PORT = 5000;

    public static void main(String[] args) {
        final ServerSocket serverSocket;
        final Socket clientSocket;
        final BufferedReader in;
        final PrintWriter out;
        final Scanner scan = new Scanner(System.in);

        try{
            serverSocket = new ServerSocket(PORT);
            System.out.println("Started server on port " + PORT);

            clientSocket = serverSocket.accept();
            System.out.println("Client connected with address: " + clientSocket.getInetAddress());

            out = new PrintWriter(clientSocket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            Thread send = new Thread(new Runnable() {
                String input;
                @Override
                public void run() {
                    while(true){
                        input = scan.nextLine();
                        if(input.startsWith("say ")){
                            out.println(input = input.substring(4));
                        } else if (input.startsWith("something ")) {
                            //idk
                        }

                        out.flush();
                    }
                }
            });
            send.start();

            Thread receive = new Thread(new Runnable() {
                String message;
                String username;
                @Override
                public void run() {
                    try{
                        username = in.readLine();
                        message = in.readLine();

                        while(message != null){
                            System.out.println(username + ": " + message);
                            message = in.readLine();
                        }

                        System.out.println("Client disconnected.");

                        out.close();
                        clientSocket.close();
                        serverSocket.close();
                    } catch (IOException e){
                        throw new RuntimeException(e);
                    }
                }
            });
            receive.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
