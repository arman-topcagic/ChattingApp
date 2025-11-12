import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.IOException;
import java.net.Socket;

public class ClientGUI {
    private final JTextArea chatArea;
    private final JTextField inputField;
    private final JButton sendButton;
    private final Client client;

    public ClientGUI(Client client){
        this.client = client;

        JFrame window = new JFrame("Chat Client - " + client.getUsername());
        window.setSize(600, 800);
        window.setLocationRelativeTo(null);
        window.setResizable(false);
        window.setLayout(new BorderLayout());
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);

        inputField = new JTextField();
        sendButton = new JButton("Send");

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        window.add(scrollPane, BorderLayout.CENTER);
        window.add(inputPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());

        window.setVisible(true);

        new Thread(() -> client.listenForMessage(chatArea)).start();
    }

    private void sendMessage() {
        String message = inputField.getText().trim();
        if (!message.isEmpty()) {
            client.sendMessage(message);
            chatArea.append("You: " + message + "\n");
            inputField.setText("");
        }
    }

    public static void main(String[] args) throws IOException {
        String username = JOptionPane.showInputDialog("Enter your desired username");
        Socket socket = new Socket("localhost", 5000);
        Client client = new Client(socket, username);

        SwingUtilities.invokeLater(() -> new ClientGUI(client));
    }
}
