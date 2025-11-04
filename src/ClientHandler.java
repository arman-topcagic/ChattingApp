import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private String username;

    public ClientHandler(Socket socket){
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            //Hämtar användarnamnet
            username = in.readLine();
            System.out.println(username+" connected to the server.");

            String message;
            while ((message = in.readLine()) != null){
                System.out.println(username + ": " + message);
            }

            System.out.println(username + " disconnected.");
            clientSocket.close();

        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
