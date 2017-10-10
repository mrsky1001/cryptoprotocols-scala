package gui;

import gui.entity.User;
import network.Client;
import network.Protocol;
import scala.Option;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Optional;

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

    private int sessionKey;
    private boolean isConnected;
    Client client;

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
                Protocol.startServer(Integer.parseInt(portField.getText()), 3, addressField.getText(), messagesPane);
            } else {
                client = Protocol.startClient(Integer.parseInt(portField.getText()), 3, addressField.getText(), messagesPane);
                try {
                    client.sendMesage(user.login());
                } catch (NullPointerException exp) {
                    connectionButton.setEnabled(true);
                    addMessage("Error, can't connection! \nPlease, try connection again.");
                }
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

    public class closedWindowListener implements WindowListener {
        public void windowClosing(WindowEvent arg0) {
            Protocol.close();
            System.exit(0);
        }

        public void windowOpened(WindowEvent arg0) {
        }

        public void windowClosed(WindowEvent arg0) {
        }

        public void windowIconified(WindowEvent arg0) {
        }

        public void windowDeiconified(WindowEvent arg0) {
        }

        public void windowActivated(WindowEvent arg0) {
        }

        public void windowDeactivated(WindowEvent arg0) {
        }
    }

    public void addListeners() {
        authorizeButton.addActionListener(new authorizeActionListener());
        connectionButton.addActionListener(new connectionActionListener());
        inputMessageButton.addActionListener(new enterMessageActionListener());
        this.addWindowListener(new closedWindowListener());
    }

    public static void main(String[] args) {
        new MainFrame().addListeners();
    }
}
