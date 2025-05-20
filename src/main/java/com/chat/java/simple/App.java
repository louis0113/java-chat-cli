package com.chat.java.simple;

import java.util.Locale;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
public class App 
{
    public static void main( String[] args ) throws Exception
    {
        Locale.setDefault(Locale.US);
        Scanner in = new Scanner(System.in);
		LocalDateTime dateAndTime = LocalDateTime.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		String dateHourFormated = dateAndTime.format(format);
		
        System.out.print("Hit your XMPP Host: ");
        String host = in.nextLine();
         
        System.out.print("Hit your XMPP domain: ");
        String dom = in.nextLine();
         
        System.out.print("Hit your username: ");
        String user = in.nextLine();
         
        System.out.print("Hit your password: ");
        String pass = in.nextLine();

        System.out.print("Hit the JID of your Friend: ");
        String friendJid = in.nextLine();
               
        User userMain = new User(user, pass);
        Server serverMain = new Server(user, pass, host, friendJid, dom);
        
               
        serverMain.createConnection();
               
        serverMain.receiveMessage();
        
        boolean executando = true;
		
   System.out.println("\n-----------------WELCOME TO CHAT------------------\n                   To exit hit '0'            \n ");
   
         do{                      
					System.out.print("\n("+ dateHourFormated
					+ ")[" + userMain.getUserName() + "@" + serverMain.getDomain() +"]: ");
					
            String mensagem = in.nextLine();
            			
			if(mensagem.equalsIgnoreCase("0")){
				executando = false;
				serverMain.closeConnection();
			} else {
				serverMain.sendMessageServer(mensagem);
			}
			
		 }while(executando);
		
        in.close();
        
        // Força o encerramento da JVM após um curto período
        // para garantir que threads residuais não impeçam o término
        System.exit(0);
    }
}
