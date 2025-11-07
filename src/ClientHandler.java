import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader in;
    private BufferedWriter out;
    private String username;

    public static ArrayList<ClientHandler> clients = new ArrayList<>();

    public ClientHandler(Socket socket){
        try {
            this.clientSocket = socket;

            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
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
                closeEverything(clientSocket, in, out);
                break;
            }
        }
    }

    public void broadcast(String sentMessage){
        for (ClientHandler clientHandler : clients){
            try {
                if(!clientHandler.username.equals(username)){
                    clientHandler.out.write(sentMessage);
                    clientHandler.out.newLine();
                    clientHandler.out.flush();
                }
            } catch (IOException e){
                closeEverything(clientSocket, in, out);
            }
        }
    }

    public void removeClients(){
        clients.remove(this);
        broadcast("SERVER: " + username + " has left the chat.");
    }

    public void closeEverything(Socket socket, BufferedReader in, BufferedWriter out){
        removeClients();
        try {
            if(in != null){
                in.close();
            }
            if(out != null){
                out.close();
            }
            if(socket != null){
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
