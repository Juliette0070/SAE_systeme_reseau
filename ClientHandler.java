import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientHandler implements Runnable {
    
    private Socket client;
    private BufferedReader reader;
    private PrintWriter writer;
    private Server server;
    private String name;

    public ClientHandler(Socket clientSocket, Server server) throws Exception{
        this.client = clientSocket;
        this.server = server;
        this.writer = new PrintWriter(this.client.getOutputStream(), true);
        this.name = "anonyme" + (this.server.getNombreConnectes()+1); //(int)(Math.random()*1000);
    }

    @Override
    public void run() {
        System.out.println(this.client.getInetAddress() + " connected.");
        try {
            this.reader = new BufferedReader(new InputStreamReader(this.client.getInputStream())); 
            while (true) {
                String message = reader.readLine();
                if (message.startsWith("/")) {
                    if (message.startsWith("/name")) {
                        this.setName(message.substring(6)); // traiter le nom
                    } else if (message.startsWith("/msg")) {
                        String[] args = message.split(" ");
                        String personne = args[1];
                        String msg = "";
                        for (int i = 2; i < args.length; i++) {
                            msg += args[i] + " ";
                        }
                        this.server.sendTo(personne, msg, this);
                    }
                } else {
                    this.server.handleMessage(message, this);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void sendMessage(String message) {
        this.writer.println(message);
    }



    @Override
    public String toString() {
        return this.name;
    }
}
