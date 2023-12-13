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
                        this.setName(message.split(" ")[1]);
                    } else if (message.startsWith("/msg")) {
                        String[] args = message.split(" ");
                        String personne = args[1];
                        String msg = "";
                        for (int i = 2; i < args.length; i++) {
                            msg += args[i] + " ";
                        }
                        this.server.sendTo(personne, msg, this);
                    } else if (message.startsWith("/follow")) {
                        String[] args = message.split(" ");
                        String personne = args[1];
                        this.server.addAbonne(personne, this);
                    } else if (message.startsWith("/unfollow")) {
                        String[] args = message.split(" ");
                        String personne = args[1];
                        this.server.removeAbonne(personne, this);
                    }else if (message.startsWith("/quit")) {
                        this.server.removeClient(this);
                        this.client.close();
                        break;
                    } else if (message.startsWith("/broadcast")) {
                        String[] args = message.split(" ");
                        String msg = "";
                        for (int i = 1; i < args.length; i++) {
                            msg += args[i] + " ";
                        }
                        this.server.broadcast(msg, this);
                    }
                } else {
                    this.server.sendToAbonnes(message, this);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setName(String name) {
        if (!this.server.nameAlreadyUsed(name)) {
            this.name = name;
        }
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
