import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

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
                    } else if (message.startsWith("/quit")) {
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
                    } else if (message.startsWith("/list")) {
                        this.afficherUtilisateurs();
                    } else {
                        this.help();
                    }
                } else {
                    this.server.sendToAbonnes(message, this);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void afficherUtilisateurs() {
        this.sendMessage("Utilisateurs connectés :");
        for (String client : this.server.getUtilisateurs()) {
            String personne = client;
            if (client.equals(this.name)) {
                personne += " (vous)";
            } else {
                boolean vousSuit = false;
                boolean suivi = false;
                for (ClientHandler abonne : this.server.getAbonnes(this)) {
                    if (abonne.getName().equals(client)) {
                        vousSuit = true;
                        break;
                    }
                } if (this.server.getAbonnes(client).contains(this)) {
                    suivi = true;
                }
                if (vousSuit && suivi) {
                    personne += " (amis)";
                } else if (vousSuit) {
                    personne += " (vous suit)";
                } else if (suivi) {
                    personne += " (suivi)";
                }
            }
            this.sendMessage(personne);
        }
    }

    public void help() {
        this.sendMessage("Commandes disponibles :");
        this.sendMessage("/name <name> : change le nom du client");
        this.sendMessage("/msg <name> <message> : envoie un message privé à <name>");
        this.sendMessage("/follow <name> : suit les messages de <name>");
        this.sendMessage("/unfollow <name> : ne suit plus les messages de <name>");
        this.sendMessage("/broadcast <message> : envoie un message à tous les clients");
        this.sendMessage("/list : affiche les utilisateurs connectés");
        this.sendMessage("/quit : quitte le serveur");
        this.sendMessage("/help : affiche les commandes");
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
