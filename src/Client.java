import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
       public static void main(String[] args) {
           final Socket clientSocket;
           final BufferedReader in;
           final PrintWriter out;
           final Scanner scan = new Scanner(System.in);

           try {
               clientSocket = new Socket("127.0.0.1", 5000);
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
                               System.out.println("Server: " + message);
                               message = in.readLine();
                           }

                           System.out.println("Server offline.");

                           out.close();
                           clientSocket.close();
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
