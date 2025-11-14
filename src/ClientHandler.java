import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * ClientHandler körs på servern för varje ansluten klient.
 * Den läser meddelanden från den klienten och skickar dem vidare till andra.
 * Den håller också en statisk lista över alla anslutna klienter så servern
 * kan skicka meddelanden till alla (broadcast).
 */
public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader in;
    private BufferedWriter out;
    private String username;

    // gemensam lista över anslutna klienter
    public static ArrayList<ClientHandler> clients = new ArrayList<>();

    /**
     * När en ny anslutning kommer, läs användarnamnet, meddela att användaren
     * anslutit och skicka en uppdaterad klientlista till alla.
     */
    public ClientHandler(Socket socket){
        try {
            this.clientSocket = socket;

            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = in.readLine();

            clients.add(this);
            broadcast("\nSERVER: " + username + " har anslutit till servern!\n");
            // skicka uppdaterad klientlista till alla
            broadcastClientList();
        } catch (Exception e) {
            closeEverything(socket, in, out);
        }
    }

    /**
     * Huvudloopen för denna klients tråd: läs rader från klienten och broadcasta dem.
     */
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

    /**
     * Skicka det angivna meddelandet till alla andra anslutna klienter.
     * Avsändaren får inte sitt eget meddelande tillbaka.
     */
    public void broadcast(String sentMessage){
        if (sentMessage == null) return; // Förhindra null-skrivningar
        for (ClientHandler clientHandler : clients){
            try {
                if (!clientHandler.username.equals(username)){
                    clientHandler.out.write(sentMessage);
                    clientHandler.out.newLine();
                    clientHandler.out.flush();
                }
            } catch (IOException e){
                removeClients();
                closeEverything(clientSocket, in, out);
            }
        }
    }

    /**
     * Ta bort denna klient från listan och meddela andra att den lämnat.
     * Efter borttagning skickas en uppdaterad klientlista.
     */
    public void removeClients(){
        broadcast("SERVER: " + username + " har lämnat chatten.");
        clients.remove(this);
        // uppdatera alla med ny lista
        broadcastClientList();
    }

    /**
     * Bygg en kommaseparerad lista med användarnamn och skicka den till alla
     * med prefixet "CLIENT_LIST:" så klienterna kan känna igen och använda den.
     */
    private void broadcastClientList() {
        try {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < clients.size(); i++) {
                sb.append(clients.get(i).username);
                if (i < clients.size() - 1) sb.append(",");
            }
            String listMessage = "CLIENT_LIST:" + sb.toString();
            for (ClientHandler clientHandler : clients) {
                try {
                    clientHandler.out.write(listMessage);
                    clientHandler.out.newLine();
                    clientHandler.out.flush();
                } catch (IOException e) {
                    // ignorera individuella fel här
                }
            }
        } catch (Exception e) {
            // ignorera
        }
    }

    /**
     * Stänger socket och strömmar för denna klient och tar bort den från listan.
     */
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
            System.out.print("Ett fel uppstod vid försök att koppla bort en klient.");
        }
    }
}
