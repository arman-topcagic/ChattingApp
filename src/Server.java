import java.net.ServerSocket;

//Remaking the server.java so that it's not as messy as the current one.
public class Server {
    private ServerSocket serverSocket;

    public Server(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }

    public void startServer(){
        try {

            while (!serverSocket.isClosed()){
                Socket socket = serverSocket.accept();
            }

        }
    }
}
