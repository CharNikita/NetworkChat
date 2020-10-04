package pm85.chat.client;

import pm85.chat.network.Connection;
import pm85.chat.network.ConnectionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class Client extends JFrame  implements ActionListener, ConnectionListener {

    private static final String IP_ADDRESS = "";
    private static final int PORT = 1234;
    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Client::new);
    }

    private final JTextArea log = new JTextArea();
    private final JTextField fieldNickName = new JTextField("Anonymous user");
    private final JTextField fieldInput = new JTextField();

    private Connection connection;

    private Client() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);

        log.setEditable(false);
        log.setLineWrap(true);
        add(log, BorderLayout.CENTER);
        fieldInput.addActionListener(this);
        add(fieldInput, BorderLayout.SOUTH);
        add(fieldNickName, BorderLayout.NORTH);

        setVisible(true);

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
            log.append(message + "\n");
            log.setCaretPosition(log.getDocument().getLength());
        });
    }

    public static String reverseString(String str) {
        return new StringBuilder(str).reverse().toString();
    }
}
