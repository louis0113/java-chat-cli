package com.chat.java.simple;
import java.util.Locale;
import java.util.Scanner;

public class App 
{
    public static void main( String[] args ) throws Exception
    {
        Locale.setDefault(Locale.US);
        Scanner in = new Scanner(System.in);
        
        
        System.out.print("Hit your XMPP Host: ");
        String host = in.nextLine();
         
        System.out.print("Hit your XMPP domain: ");
        String dom = in.nextLine();
         
        System.out.print("Hit your username: ");
        String user = in.nextLine();
         
        System.out.print("Hit your password: ");
        String pass = in.nextLine();

        System.out.print("Hit your friend JID: ");
        String friendJid = in.nextLine();
        
      
        User userMain = new User(user, pass);
        Server serverMain = new Server(user, pass, host, friendJid, dom);
        
        
        serverMain.configurarServer();
        serverMain.criarConexao();
        
       
        System.out.print("Mande uma mensagem: ");
        String mensagem = in.nextLine();
        serverMain.mandarMensagemServer(mensagem);
        
        System.out.println("Mensagem enviada com sucesso!");
        
        
        in.close();
    }
}
