import javax.swing.*;
import java.io.*;
import java.net.Socket;

public class Client {

    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private String username;

    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.username = username;
        } catch (Exception e) {
            closeEverything(socket, in, out);
        }
    }

    public void sendMessage(String message) {
        try {
            out.write(username + ": " + message);
            out.newLine();
            out.flush();
        } catch (Exception e) {
            closeEverything(socket, in, out);
        }
    }

    public void listenForMessage(JTextArea chatArea) {
        try {
            String messageFromChat;
            while ((messageFromChat = in.readLine()) != null) {
                String finalMessage = messageFromChat;
                SwingUtilities.invokeLater(() -> chatArea.append(finalMessage + "\n"));
            }
        } catch (IOException e) {
            closeEverything(socket, in, out);
        }
    }

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

    public String getUsername() {
        return username;
    }
}
