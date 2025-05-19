package com.chat.java.simple;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.EntityBareJid;
import org.jivesoftware.smack.roster.Roster;

public class Server extends User {
    
    private String host, friendHost, domain;
    private AbstractXMPPConnection connection;
    private Chat chatInstance;
    
    public Server(String userName, String password, String host, String friendHost, String domain) {
        super(userName, password);
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
            .setUsernameAndPassword(getUserName(), getPassword())
            .setXmppDomain(domain)
            .setHost(host)
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
                System.out.println("Chat conectado com: " + conectJID);
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
            System.out.println("Mensagem enviada para: " + friendHost);			
        } catch (Exception e) {
            System.err.println("Erro ao enviar mensagem: " + e.getMessage());
            throw e;
        }
    }
	public void receberMensagem(){
		System.out.println("Funcionalidade aind não implementada");
	}
	
    public void criarGrupo() {
        
        System.out.println("Funcionalidade ainda não implementada");
    }

    public void mostrarPresenca() {
        
        System.out.println("Funcionalidade ainda não implementada");
    }
    
    // Método para fechar a conexão
    public void fecharConexao() {
        if (connection != null && connection.isConnected()) {
            connection.disconnect();
            System.out.println("Conexão fechada");
        }
    }
}
