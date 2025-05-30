package com.chat.java.simple;

import java.util.Locale;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicBoolean;

public class App 
{
    public static void main( String[] args ) throws Exception
    {
        Locale.setDefault(Locale.US);
        Scanner in = new Scanner(System.in);
               
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║           XMPP CHAT - LOGIN              ║");
        System.out.println("╚══════════════════════════════════════════╝");
        
        System.out.print("➤ XMPP Host: ");
        String host = in.nextLine();
         
        System.out.print("➤ XMPP Domain: ");
        String dom = in.nextLine();
         
        System.out.print("➤ Username: ");
        String user = in.nextLine();
         
        System.out.print("➤ Password: ");
        String pass = in.nextLine();

        System.out.print("➤ Friend's JID: ");
        String friendJid = in.nextLine();
               
        User userMain = new User(user, pass);
        final Server serverMain = new Server(user, pass, host, friendJid, dom);
        
        System.out.println("\n⌛ Connecting to server...");
        serverMain.createConnection();
        serverMain.receiveMessage();
        
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║               XMPP CHAT                  ║");
        System.out.println("║                                          ║");
        System.out.println("║  Commands:                               ║");
        System.out.println("║    /exit - Exit the chat                 ║");
        System.out.println("║    /help - Show commands                 ║");
		System.out.println("║    /info - Show user information         ║");
        System.out.println("╚══════════════════════════════════════════╝");
        
        final AtomicBoolean running = new AtomicBoolean(true);
        
        Thread inputThread = new Thread(() -> {
            try {
                do {
                   
                    LocalDateTime dateAndTime = LocalDateTime.now();
                    DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm:ss");
                    String timeFormatted = dateAndTime.format(format);
                    
                    System.out.print("\n[" + timeFormatted + "] You: ");
                    
                    String message = in.nextLine();
                    
                    if (message.equalsIgnoreCase("/exit") || message.equals("0")) {
                        System.out.println("\n→ Disconnecting from chat...");
                        running.set(false);
                        serverMain.closeConnection();
                        break;
                    } else if (message.equalsIgnoreCase("/help")) {
                        System.out.println("\n╔══════════════════════════════════════════╗");
                        System.out.println("║  Available Commands:                     ║");
                        System.out.println("║    /exit - Exit the chat                 ║");
                        System.out.println("║    /help - Show this help message        ║");
						 System.out.println("║    /info - Show user info                ║");
                        System.out.println("╚══════════════════════════════════════════╝");
                    } else if(message.equalsIgnoreCase("/info")){
					   System.out.println("----------------------------------------");
						 System.out.println("Show users informations now:");
					   System.out.println("\nName: " + userMain.getUserName() +
										  "\nPassword " + userMain.getPassword());
					   System.out.println("----------------------------------------");
					} else if (!message.trim().isEmpty()) {
                      
                        System.out.println("  ↪ " + message);
                        serverMain.sendMessageServer(message);
                    }
                    
                } while(running.get());
            } catch (Exception e) {
                System.out.println("\n❌ Error processing input: " + e.getMessage());
                running.set(false);
            }
        });
        
        inputThread.start();
        
       
        try {
            while(running.get()) {
                Thread.sleep(500);
            }
        } catch (InterruptedException e) {
            System.out.println("Main thread interrupted.");
        }
        
        in.close();
               
        System.exit(0);
    }
}
