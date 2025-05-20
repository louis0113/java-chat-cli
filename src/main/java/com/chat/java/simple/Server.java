package com.chat.java.simple;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Server {
    
    private String host, friendHost, domain;
	private User user;
    private AbstractXMPPConnection connection;
    private Chat chatInstance;
    private boolean receiveMessages = false;
    private Thread messageReceiverThread;
    private volatile boolean messageReceived = false;
    private volatile boolean firstMessage = true;
    
    public Server(String userName, String password, String host, String friendHost, String domain) {
		this.user = new User(userName, password);
        this.host = host;
        this.friendHost = friendHost;
        this.domain = domain;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getFriendHost() {
        return friendHost;
    }

    public void setFriendHost(String friendHost) {
        this.friendHost = friendHost;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
         
    public XMPPTCPConnectionConfiguration configureServer() throws Exception {
        XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
            .setUsernameAndPassword(user.getUserName(), user.getPassword())
            .setXmppDomain(this.domain)
            .setHost(this.host)
            .setResource("JavaSimpleClient")  // Adicionado recurso para identificação
            .build();
            
        System.out.println("Server configured successfully");
        return config;    
    }

    public AbstractXMPPConnection createConnection() throws Exception {
        if (connection == null || !connection.isConnected()) {
            try {
                connection = new XMPPTCPConnection(configureServer());
                connection.connect();
                connection.login();
                System.out.println("Successfully connected to server!");
            } catch (Exception e) {
                System.err.println("Error connecting: " + e.getMessage());
                throw e;
            }
        }
        return connection;
    }
    
    public Chat connectChat(String connectJID) throws Exception {
        if (chatInstance == null) {
            try {
                ChatManager chatManager = ChatManager.getInstanceFor(createConnection());
                EntityBareJid jid = JidCreate.entityBareFrom(connectJID);
                chatInstance = chatManager.chatWith(jid);
               
            } catch (Exception e) {
                System.err.println("Error connecting to chat: " + e.getMessage());
                throw e;
            }
        }
        return chatInstance;
    }

    public void sendMessageServer(String message) throws Exception {
        try {
            // Na primeira mensagem, permite enviar sem esperar
            if (firstMessage) {
                firstMessage = false;
            } else {
                // Espera até que uma mensagem seja recebida
                while (!messageReceived) {
                    Thread.sleep(100); // Espera um pouco antes de verificar novamente
                }
                messageReceived = false; // Reseta o flag para a próxima mensagem
            }
            
            Chat chat = connectChat(friendHost);
            chat.send(message);
            
        } catch (Exception e) {
            System.err.println("Error sending message: " + e.getMessage());
            throw e;
        }
    }
    

    public void receiveMessage() {
        try {
			LocalDateTime dateAndTime = LocalDateTime.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		String dateHourFormated = dateAndTime.format(format);
            // Ensure we are connected
            AbstractXMPPConnection conn = createConnection();
            
            // Get the chat manager
            ChatManager chatManager = ChatManager.getInstanceFor(conn);
            
            // Configure a listener for incoming messages
            chatManager.addIncomingListener(new IncomingChatMessageListener() {
                @Override
                public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {
                    System.out.print("\n(" + dateHourFormated + ")" + "[" + from.toString() + "]: ");
                    System.out.println(message.getBody());
                    messageReceived = true; // Marca que uma mensagem foi recebida
                }
            });
                
            if (messageReceiverThread == null || !messageReceiverThread.isAlive()) {
                receiveMessages = true;
                messageReceiverThread = new Thread(() -> {
                    try {
                        while (receiveMessages) {
                            Thread.sleep(500); // Wait 500ms between checks
                        }
                    } catch (InterruptedException e) {
                        System.out.println("Message receiver interrupted.");
                    }
                });
                messageReceiverThread.setDaemon(true); // Background thread
                messageReceiverThread.start();
            }
            
        } catch (Exception e) {
            System.err.println("Error configuring message receiver: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void closeConnection() {
        receiveMessages = false;
        if (messageReceiverThread != null && messageReceiverThread.isAlive()) {
            messageReceiverThread.interrupt();
        }
        
        if (connection != null && connection.isConnected()) {
            connection.disconnect();
            System.out.println("Connection closed");
        }
    }
}
