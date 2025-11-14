import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Server lyssnar efter klientanslutningar på PORT och
 * startar en ClientHandler-tråd för varje ansluten klient.
 * Server-klassen håller främst ServerSocket och huvudloopen.
 */
public class Server {
    private ServerSocket serverSocket;
    private static final int PORT = 5000;

    public Server(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }

    /**
     * Acceptera anslutningar i en loop. För varje ny socket, skapa och starta en ClientHandler.
     */
    public void startServer(){
        try {

            while (!serverSocket.isClosed()){
                Socket socket = serverSocket.accept();
                System.out.println("A new client connected from " + socket.getInetAddress());
                ClientHandler clientHandler = new ClientHandler(socket);

                Thread thread = new Thread(clientHandler);
                thread.start();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void closeServerSocket(){
        try {
            if (serverSocket != null){
                serverSocket.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Programstart för att köra servern. Kör på port 5000.
     */
    public static void main (String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        Server server = new Server(serverSocket);
        server.startServer();


    }
}
