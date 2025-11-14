import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.IOException;
import java.net.Socket;

/**
 * Enkel Swing-GUI för chattklienten.
 * Visar ett chattfönster (meddelanden), en lista över anslutna klienter,
 * samt ett inmatningsfält och en skicka-knapp.
 */
public class ClientGUI {
    private final JTextArea chatArea;
    private final JTextArea clientArea;
    private final JTextField inputField;
    private final JButton sendButton;
    private final Client client;

    /**
     * Bygger GUI:t och startar en bakgrundstråd som lyssnar efter servermeddelanden.
     * @param client redan ansluten Client-instans
     */
    public ClientGUI(Client client){
        this.client = client;

        JFrame window = new JFrame("Chat Client - " + client.getUsername());
        window.setSize(800, 800);
        window.setLocationRelativeTo(null);
        window.setResizable(false);
        window.setLayout(new BorderLayout());
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // huvudchattområde (center)
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setBackground(Color.decode("#1e1f22"));
        chatArea.setForeground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(chatArea);

        // klientlista (höger)
        clientArea = new JTextArea();
        clientArea.setPreferredSize(new Dimension(200, 800));
        clientArea.setEditable(false);
        clientArea.setBackground(Color.decode("#1e1f22"));
        clientArea.setForeground(Color.WHITE);
        JScrollPane scrollPane1 = new JScrollPane(clientArea);
        scrollPane1.setPreferredSize(new Dimension(200, 0));
        // ge en tydlig rubrik åt listan
        scrollPane1.setBorder(BorderFactory.createTitledBorder("Anslutna klienter"));

        // inmatningsområde (botten)
        inputField = new JTextField();
        inputField.setPreferredSize(new Dimension(0,60));
        inputField.setBackground(Color.decode("#2b2d30"));
        inputField.setForeground(Color.WHITE);
        sendButton = new JButton("( Send )");
        sendButton.setBackground(Color.decode("#3e80d7"));

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        window.add(scrollPane, BorderLayout.CENTER);
        window.add(inputPanel, BorderLayout.SOUTH);
        // använd scrollpanelen för klientlistan
        window.add(scrollPane1, BorderLayout.EAST);

        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());

        window.setVisible(true);

        // starta lyssnare för inkommande meddelanden från servern
        new Thread(() -> client.listenForMessage(chatArea, clientArea)).start();
    }

    /**
     * Skickar texten från inmatningsfältet till servern och rensar fältet.
     */
    private void sendMessage() {
        String message = inputField.getText().trim();
        if (!message.isEmpty()) {
            client.sendMessage(message);
            chatArea.append("You: " + message + "\n");
            inputField.setText("");
        }
    }

    /**
     * Startpunkt för GUI-klienten. Frågar efter användarnamn och ansluter till localhost:5000.
     */
    public static void main(String[] args) throws IOException {
        String username = JOptionPane.showInputDialog("Enter your desired username");
        Socket socket = new Socket("localhost", 5000);
        Client client = new Client(socket, username);

        SwingUtilities.invokeLater(() -> new ClientGUI(client));
    }
}
