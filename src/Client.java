import javax.swing.*;
import java.io.*;
import java.net.Socket;

/**
 * Client representerar en anslutning från GUI:t till chattservern.
 * Den skickar användarnamnet och meddelanden till servern,
 * och lyssnar efter meddelanden från servern.
 */
public class Client {

    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private String username;

    /**
     * Skapar en Client och skickar användarnamnet till servern direkt.
     * @param socket socket som är ansluten till servern
     * @param username användarnamnet som valts av användaren
     */
    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.username = username;
            // skicka användarnamnet följt av radslut så servern kan läsa det med readLine()
            out.write(username);
            out.newLine();
            out.flush();
        } catch (Exception e) {
            closeEverything(socket, in, out);
        }
    }

    /**
     * Skickar ett chattmeddelande till servern.
     * Meddelandeformat: "username: message"
     */
    public void sendMessage(String message) {
        try {
            out.write(username + ": " + message);
            out.newLine();
            out.flush();
        } catch (Exception e) {
            closeEverything(socket, in, out);
        }
    }

    /**
     * Lyssnar efter inkommande meddelanden från servern.
     * Det finns två typer av rader:
     * - Rader som börjar med "CLIENT_LIST:" innehåller en kommaseparerad lista
     *   med användarnamn. Dessa uppdaterar clientArea (en rad per användare).
     * - Alla andra rader är vanliga chattmeddelanden och visas i chatArea.
     *
     * Metoden körs i en bakgrundstråd och uppdaterar Swing-komponenter på UI-tråden.
     */
    public void listenForMessage(JTextArea chatArea, JTextArea clientArea) {
        try {
            String messageFromChat;
            while ((messageFromChat = in.readLine()) != null) {
                String finalMessage = messageFromChat;
                // speciellt protokoll: server skickar en klientlista med prefix "CLIENT_LIST:"
                if (finalMessage.startsWith("CLIENT_LIST:")) {
                    String listPart = finalMessage.substring("CLIENT_LIST:".length());
                    // klienter förväntas komma-separerade
                    String[] users = listPart.split(",");
                    SwingUtilities.invokeLater(() -> {
                        clientArea.setText("");
                        for (String u : users) {
                            String trimmed = u.trim();
                            if (!trimmed.isEmpty()) {
                                clientArea.append(trimmed + "\n");
                            }
                        }
                    });
                } else {
                    // vanligt chattmeddelande
                    SwingUtilities.invokeLater(() -> chatArea.append(finalMessage + "\n"));
                }
            }
        } catch (IOException e) {
            closeEverything(socket, in, out);
        }
    }

    /**
     * Stänger socket och strömmar vid avslut eller fel.
     */
    public void closeEverything(Socket socket, BufferedReader in, BufferedWriter out) {
        try {
            if (in != null){
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returnerar användarnamnet för denna klient.
     */
    public String getUsername() {
        return username;
    }
}
