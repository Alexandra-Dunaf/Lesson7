package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Chat extends JFrame {

    private JTextField message;
    private JTextArea chatArea;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String nick = "";







    public Chat() {
        // Параметры окна
        setBounds(600, 300, 500, 500);
        setTitle("Чат");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Текстовое поле для вывода сообщений
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        // Нижняя панель с полем для ввода сообщений и кнопкой отправки сообщений
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JButton btnSendMsg = new JButton("Отправить");
        bottomPanel.add(btnSendMsg, BorderLayout.EAST);
        message = new JTextField();
        add(bottomPanel, BorderLayout.SOUTH);
        bottomPanel.add(message, BorderLayout.CENTER);
        btnSendMsg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        message.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        message.setText("/auth login1 pass1");


        // Настраиваем действие на закрытие окна
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                try {
                    out.writeUTF("/end");
                    closeConnection();
                } catch (IOException exc) {
                    exc.printStackTrace();
                }
            }
        });

        setVisible(true);
    }

    public void closeConnection() {
        chatArea.append("Вы вышли из чата \n");
        message.setText("/auth login1 pass1");
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        new Chat();

    }

    public void sendMessage() {
        String msgInputField = this.message.getText().trim();
        if (!msgInputField.isEmpty()) {
            message.setText("");
            try {
                if (socket == null || socket.isClosed()) {
                    socket = new Socket("localhost", 8189);
                    in = new DataInputStream(socket.getInputStream());
                    out = new DataOutputStream(socket.getOutputStream());
                    new Thread(() -> {
                        try {
                            while (true) {
                                String strFromServer = in.readUTF();
                                if (strFromServer.startsWith("/authok")) {
                                    nick = strFromServer.split(" ")[1];
                                    chatArea.append("Вы авторизованы как " + nick + "/n ");
                                    break;
                                }
                                chatArea.append(strFromServer + "/n");
                            }
                            while (true) {
                                String strFromServer = in.readUTF();
                                if (strFromServer.equalsIgnoreCase("/end")) {
                                    break;
                                }
                                chatArea.append(strFromServer + "/n ");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            closeConnection();
                        }
                    }).start();
                }
                out.writeUTF(msgInputField);
            } catch (IOException e) {
                e.printStackTrace();



            }


        }
    }
}







