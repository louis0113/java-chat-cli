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
         
    public XMPPTCPConnectionConfiguration configurarServer() throws Exception {
        XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
            .setUsernameAndPassword(user.getUserName(), user.getPassword())
            .setXmppDomain(this.domain)
            .setHost(this.host)
            .setResource("JavaSimpleClient")  // Adicionado recurso para identificação
            .build();
            
        System.out.println("Servidor configurado com sucesso");
        return config;    
    }

    public AbstractXMPPConnection criarConexao() throws Exception {
        if (connection == null || !connection.isConnected()) {
            try {
                connection = new XMPPTCPConnection(configurarServer());
                connection.connect();
                connection.login();
                System.out.println("Conectado ao servidor com sucesso!");
            } catch (Exception e) {
                System.err.println("Erro ao conectar: " + e.getMessage());
                throw e;
            }
        }
        return connection;
    }
    
    public Chat conectarChat(String conectJID) throws Exception {
        if (chatInstance == null) {
            try {
                ChatManager chatManager = ChatManager.getInstanceFor(criarConexao());
                EntityBareJid jid = JidCreate.entityBareFrom(conectJID);
                chatInstance = chatManager.chatWith(jid);
               
            } catch (Exception e) {
                System.err.println("Erro ao conectar chat: " + e.getMessage());
                throw e;
            }
        }
        return chatInstance;
    }

    public void mandarMensagemServer(String mensagem) throws Exception {
        try {
            Chat chat = conectarChat(friendHost);
            chat.send(mensagem);
            
        } catch (Exception e) {
            System.err.println("Erro ao enviar mensagem: " + e.getMessage());
            throw e;
        }
    }
    

    public void receberMensagem() {
        try {
			LocalDateTime dateAndTime = LocalDateTime.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		String dateHourFormated = dateAndTime.format(format);
            // Garantir que estamos conectados
            AbstractXMPPConnection conn = criarConexao();
            
            // Obter o gerenciador de chat
            ChatManager chatManager = ChatManager.getInstanceFor(conn);
            
            // Configurar um ouvinte para mensagens de entrada
            chatManager.addIncomingListener(new IncomingChatMessageListener() {
                @Override
                public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {
                    System.out.print("\n(" + dateHourFormated + ")" + "[" + from.toString() + "]: ");
                    System.out.println( message.getBody());
                }
            });
                
            if (messageReceiverThread == null || !messageReceiverThread.isAlive()) {
                receiveMessages = true;
                messageReceiverThread = new Thread(() -> {
                    try {
                        while (receiveMessages) {
                            Thread.sleep(500); // Aguarda 500ms entre verificações
                        }
                    } catch (InterruptedException e) {
                        System.out.println("Receptor de mensagens interrompido.");
                    }
                });
                messageReceiverThread.setDaemon(true); // Thread em segundo plano
                messageReceiverThread.start();
            }
            
        } catch (Exception e) {
            System.err.println("Erro ao configurar receptor de mensagens: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void fecharConexao() {
        receiveMessages = false;
        if (messageReceiverThread != null && messageReceiverThread.isAlive()) {
            messageReceiverThread.interrupt();
        }
        
        if (connection != null && connection.isConnected()) {
            connection.disconnect();
            System.out.println("Conexão fechada");
        }
    }
}
