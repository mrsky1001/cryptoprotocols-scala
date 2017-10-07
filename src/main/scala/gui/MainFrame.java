package gui;

import gui.entity.User;
import network.Client;
import network.Protocol;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {
    private JPanel mainPanel;
    private JPanel settings;
    private JPanel authorize;
    private JLabel loginLabel;
    private JLabel passwordLabel;
    private JTextField loginField;
    private JTextField passwordField;
    private JButton authorizeButton;

    private JPanel connection;
    private JLabel addressLabel;
    private JLabel portLabel;
    private JTextField addressField;
    private JTextField portField;
    private JButton connectionButton;

    private JPanel messages;
    private JTextPane messagesPane;
    private JTextField inputMessageField;
    private JButton inputMessageButton;
    private User user;

    //===========================
    private int sessionKey;
    private boolean isConnected;
    Client client;
    Client server;

    public MainFrame() {
        setContentPane(mainPanel);
        setVisible(true);
        int widthForm = 500;
        int heightForm = 400;
        setSize(widthForm, heightForm);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public void addMessage(Object message) {
        messagesPane.setText(messagesPane.getText() + "\n" + message);
    }

    public class authorizeActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (user != null)
                addMessage("Goodbye, " + user.login() + "!!!");

            user = new User(loginField.getText(), passwordField.getText());
            addMessage("Welcome, " + user.login() + "!!!");
            if (user.login().equalsIgnoreCase("trent")) {
                inputMessageButton.setEnabled(false);
            }
            authorizeButton.setEnabled(false);
        }
    }

    public class connectionActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (user == null) {
                messagesPane.setText("Please first sign in!");
                return;
            }
            connectionButton.setEnabled(false);
            addMessage(user.login() + " connection...");
            if (user.login().equalsIgnoreCase("trent")) {
                server = Protocol.startServer(Integer.parseInt(portField.getText()), 3, addressField.getText(), messagesPane);
            } else {
                client = Protocol.startClient(Integer.parseInt(portField.getText()), 3, addressField.getText(), messagesPane);
                client.sendMesage(user.login());
                client.sendMesage("Hi!!!!");
            }
        }
    }

    public class enterMessageActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            addMessage(inputMessageField.getText());
            if (client != null)
                client.sendMesage(inputMessageField.getText());
            else
                addMessage("Error, Client don't connected!!!");
        }
    }

    public void addListeners() {
        ActionListener authorizeActionListener = new authorizeActionListener();
        ActionListener connectionActionListener = new connectionActionListener();
        ActionListener enterMessageActionListener = new enterMessageActionListener();

        authorizeButton.addActionListener(authorizeActionListener);
        connectionButton.addActionListener(connectionActionListener);
        inputMessageButton.addActionListener(enterMessageActionListener);
    }

    public static void main(String[] args) {
        new MainFrame().addListeners();
    }
}
