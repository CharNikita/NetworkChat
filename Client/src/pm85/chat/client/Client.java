package pm85.chat.client;

import pm85.chat.network.Connection;
import pm85.chat.network.ConnectionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class Client extends JFrame  implements ActionListener, ConnectionListener {

    private static String IP_ADDRESS = null;
    private static final int PORT = 2002;
    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Client::new);
    }

    private final JTextArea chatLog = new JTextArea();
    private final JTextField fieldNickName = new JTextField("Anonymous user");
    private final JTextField fieldInput = new JTextField();

    private Connection connection;

    private Client() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);

        chatLog.setEditable(false);
        chatLog.setLineWrap(true);
        add(chatLog, BorderLayout.CENTER);
        fieldInput.addActionListener(this);
        add(fieldInput, BorderLayout.SOUTH);
        add(fieldNickName, BorderLayout.NORTH);

        setVisible(true);
        while (IP_ADDRESS == null) {
            IP_ADDRESS = getIPModal();
        }

        try {
            connection = new Connection(this, IP_ADDRESS, PORT);
        } catch (IOException e) {
            printMessage("Connection exception: " + e);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String message = fieldInput.getText();
        if (message.equals("")) {
            return;
        }
        fieldInput.setText(null);
        message = fieldNickName.getText() + ": " + reverseString(message);
        connection.sendMessage(message);
    }

    String getIPModal() {
        return (String)JOptionPane.showInputDialog(
                chatLog,
                "Введите IP-адрес сервера:\n",
                "Customized Dialog",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                "0.0.0.0");
    }

    @Override
    public void onConnectionReady(Connection connection) {
        printMessage("Connection ready!");
    }

    @Override
    public void onReceiveString(Connection connection, String string) {
        printMessage(string);
    }

    @Override
    public void onDisconnect(Connection connection) {
        printMessage("Connection close!");
    }

    @Override
    public void onException(Connection connection, Exception e) {
        printMessage("Connection exception: " + e);
    }

    private synchronized void printMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            chatLog.append(message + "\n");
            chatLog.setCaretPosition(chatLog.getDocument().getLength());
        });
    }

    public static String reverseString(String str) {
        return new StringBuilder(str).reverse().toString();
    }
}
