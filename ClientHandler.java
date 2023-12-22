import java.io.BufferedReader;
import java.io.IOException;
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
    private Utilisateur utilisateur;

    public ClientHandler(Socket clientSocket, Server server) throws Exception{
        this.client = clientSocket;
        this.server = server;
        this.writer = new PrintWriter(this.client.getOutputStream(), true);
        this.name = "anonyme" + (this.server.getNombreConnectes()+1); //(int)(Math.random()*1000);
        this.utilisateur = null;
    }

    @Override
    public void run() {
        System.out.println(this.client.getInetAddress() + " connected.");
        try {
            this.reader = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
            while (this.utilisateur == null) {
                try {
                    this.recupererUtilisateur();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } this.sendMessage("Bienvenue " + this.utilisateur.getPseudo() + " !");
            while (true) {
                String message = reader.readLine();
                if (message == null || message.equals("")) {
                    continue;
                }
                if (message.startsWith("/")) {
                    this.handleCommande(message);
                } else {
                    this.server.sendToAbonnes(message, this);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void recupererUtilisateur() throws IOException {
        this.sendMessage("Pseudo:");
        String pseudo = this.reader.readLine();
        this.sendMessage("Mot de passe:");
        String motDePasse = this.reader.readLine();
        this.utilisateur = this.server.getUtilisateur(pseudo);
        if (this.utilisateur == null) {
            this.utilisateur = new Utilisateur(pseudo, motDePasse);
            this.server.addUtilisateur(this.utilisateur);
        } else if (!this.utilisateur.getMotDePasse().equals(motDePasse)) {
            this.utilisateur = null;
        }
    }

    public void handleCommande(String commande) throws IOException {
        if (commande.startsWith("/name")) {
            this.setName(commande.split(" ")[1]);
        } else if (commande.startsWith("/msg")) {
            String[] args = commande.split(" ");
            String personne = args[1];
            String msg = "";
            for (int i = 2; i < args.length; i++) {msg += args[i] + " ";}
            this.server.sendTo(personne, msg, this);
        } else if (commande.startsWith("/follow")) {
            String[] args = commande.split(" ");
            String personne = args[1];
            this.server.addAbonne(personne, this);
        } else if (commande.startsWith("/unfollow")) {
            String[] args = commande.split(" ");
            String personne = args[1];
            this.server.removeAbonne(personne, this);
        } else if (commande.startsWith("/quit")) {
            this.server.removeClient(this);
            this.client.close();
        } else if (commande.startsWith("/broadcast")) {
            String[] args = commande.split(" ");
            String msg = "";
            for (int i = 1; i < args.length; i++) {
                msg += args[i] + " ";
            }
            this.server.broadcast(msg, this);
        } else if (commande.startsWith("/list")) {
            this.afficherUtilisateurs();
        } else {
            this.help();
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
