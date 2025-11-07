import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private String username;

    public static ArrayList<ClientHandler> clients = new ArrayList<>();

    public ClientHandler(Socket socket){
        try {
            this.clientSocket = socket;

            this.out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = in.readLine();

            clients.add(this);
            broadcast("SERVER: " + username + " has connected to the server!");
        } catch (Exception e) {
            closeEverything(socket, in, out);
        }
    }

    @Override
    public void run() {
        while(clientSocket.isConnected()){
            try {
                String message;

                message = in.readLine();
                broadcast(message);

            } catch (IOException e){
                closeEverything(socket, in, out);
                break;
            }
        }
    }

    public void broadcast(String sentMessage){
        for (ClientHandler clientHandler : clients){
            if(!clientHandler.username.equals(username)){

            }
        }
    }
}
