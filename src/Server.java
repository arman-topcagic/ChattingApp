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
            clientSocket = serverSocket.accept();
            out = new PrintWriter(clientSocket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            Thread send = new Thread(new Runnable() {
                String message;
                @Override
                public void run() {
                    while(true){
                        message = scan.nextLine();
                        out.println(message);
                        out.flush();
                    }
                }
            });
            send.start();

            Thread receive = new Thread(new Runnable() {
                String message;
                @Override
                public void run() {
                    try{
                        message = in.readLine();

                        while(message != null){
                            System.out.println("Client: " + message);
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
