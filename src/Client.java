import java.io.*;
import java.net.Socket;
import java.util.Scanner;

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

    public void sendMessage() {
        try {
            out.write(username);
            out.newLine();
            out.flush();

            Scanner scan = new Scanner(System.in);
            while (socket.isConnected()) {
                String messageToSend = scan.nextLine();
                out.write(username + ": " + messageToSend);
                out.newLine();
                out.flush();
            }
        } catch (Exception e) {
            closeEverything(socket, in, out);
        }
    }

    public void listenForMessage() {
        new Thread(new Runnable() {
            @Override
            public void run(){
                String messageFromChat;

                while (socket.isConnected()) {
                    try {
                        messageFromChat = in.readLine();
                        System.out.println(messageFromChat);
                    } catch (IOException e) {
                        closeEverything(socket, in, out);
                    }
                }
            }
        }).start();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {

        Scanner scan = new Scanner(System.in);
        System.out.println("Enter username: ");
        String username = scan.nextLine();
        Socket socket = new Socket("localhost", 5000);
        Client client = new Client(socket, username);
        client.listenForMessage();
        client.sendMessage();
    }

}