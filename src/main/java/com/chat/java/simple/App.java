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
        
        // Interface de login com estilo melhorado
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
        Server serverMain = new Server(user, pass, host, friendJid, dom);
        
        System.out.println("\n⌛ Connecting to server...");
        serverMain.createConnection();
        serverMain.receiveMessage();
        
        // Interface do chat melhorada
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║               XMPP CHAT                  ║");
        System.out.println("║                                          ║");
        System.out.println("║  Commands:                               ║");
        System.out.println("║    /exit - Exit the chat                 ║");
        System.out.println("║    /help - Show commands                 ║");
        System.out.println("╚══════════════════════════════════════════╝");
        
        boolean executando = true;
        
        // Cria um thread separado para lidar com a entrada do usuário
        Thread inputThread = new Thread(() -> {
            try {
                do {
                    // Atualiza a data/hora a cada mensagem
                    LocalDateTime dateAndTime = LocalDateTime.now();
                    DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm:ss");
                    String timeFormatted = dateAndTime.format(format);
                    
                    System.out.print("\n[" + timeFormatted + "] You: ");
                    
                    String mensagem = in.nextLine();
                    
                    if (mensagem.equalsIgnoreCase("/exit") || mensagem.equals("0")) {
                        System.out.println("\n→ Disconnecting from chat...");
                        executando = false;
                        serverMain.closeConnection();
                        break;
                    } else if (mensagem.equalsIgnoreCase("/help")) {
                        System.out.println("\n╔══════════════════════════════════════════╗");
                        System.out.println("║  Available Commands:                      ║");
                        System.out.println("║    /exit - Exit the chat                  ║");
                        System.out.println("║    /help - Show this help message         ║");
                        System.out.println("╚══════════════════════════════════════════╝");
                    } else if (!mensagem.trim().isEmpty()) {
                        // Mostra a mensagem enviada com formatação
                        System.out.println("  ↪ " + mensagem);
                        serverMain.sendMessageServer(mensagem);
                    }
                    
                } while(executando);
            } catch (Exception e) {
                System.out.println("\n❌ Erro no processamento de entrada: " + e.getMessage());
                executando = false;
            }
        });
        
        inputThread.start();
        
        // Thread principal aguarda até que o chat seja encerrado
        try {
            while(executando) {
                Thread.sleep(500);
            }
        } catch (InterruptedException e) {
            System.out.println("Thread principal interrompida.");
        }
        
        in.close();
        
        // Força o encerramento da JVM após um curto período
        System.exit(0);
    }
}
