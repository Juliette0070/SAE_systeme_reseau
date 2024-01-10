import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientHandler implements Runnable {
    
    private Socket client;
    private BufferedReader reader;
    private PrintWriter writer;
    private Server server;
    private Utilisateur utilisateur;

    public ClientHandler(Socket clientSocket, Server server) throws Exception{
        this.client = clientSocket;
        this.server = server;
        this.writer = new PrintWriter(this.client.getOutputStream(), true);
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
            }
            this.utilisateur.setConnecte(true);
            this.sendMessageFromServer("Bienvenue " + this.utilisateur.getPseudo() + " !");
            while (this.utilisateur.isConnecte()) {
                String message = reader.readLine();
                if (message == null || message.equals("")) {
                    continue;
                }
                if (message.startsWith("/")) {
                    this.handleCommande(message);
                } else {
                    this.server.sendToAbonnes(this.server.createMessage(message, this.utilisateur));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(this.client.getInetAddress() + " encountered an error.");
        }
        System.out.println(this.client.getInetAddress() + " disconnected.");
    }

    public Utilisateur getUtilisateur() {
        return this.utilisateur;
    }

    public void recupererUtilisateur() throws IOException {
        this.sendMessageFromServer("Pseudo:");
        String pseudo = this.reader.readLine();
        this.sendMessageFromServer("Mot de passe:");
        String motDePasse = this.reader.readLine();
        this.utilisateur = this.server.getUtilisateur(pseudo);
        if (this.utilisateur == null) {
            this.sendMessageFromServer("Cet utilisateur n'existe pas, voulez-vous le créer ? (o/n)");
            String reponse = this.reader.readLine();
            if (reponse.equals("o") || reponse.equals("oui")) {
                this.utilisateur = new Utilisateur(pseudo, motDePasse);
                this.server.addUtilisateur(this.utilisateur);
                this.sendMessageFromServer("Utilisateur créé !");
            }
        } else if (this.utilisateur.isConnecte()) {
            this.utilisateur = null;
            this.sendMessageFromServer("Cet utilisateur est déjà connecté !");
        } else if (!this.utilisateur.getMotDePasse().equals(motDePasse)) {
            this.utilisateur = null;
            this.sendMessageFromServer("Mot de passe incorrect !");
        }
    }

    public void handleCommande(String commande) throws IOException {
        if (commande.startsWith("/name")) {
            String nom = commande.split(" ")[1];
            if (this.server.getUtilisateur(nom) == null) {
                this.utilisateur.setPseudo(nom);
            } else {
                this.sendMessageFromServer("Ce pseudo est déjà utilisé !");
            }
        } else if (commande.startsWith("/msg")) {
            String[] args = commande.split(" ");
            String personne = args[1];
            String msg = "";
            for (int i = 2; i < args.length; i++) {msg += args[i] + " ";}
            this.server.sendTo(personne, this.server.createMessage(msg, this.utilisateur));
        } else if (commande.startsWith("/follow")) {
            String[] args = commande.split(" ");
            String personne = args[1];
            this.utilisateur.addAbonne(this.server.getUtilisateur(personne));
            this.sendMessageFromServer("Vous suivez désormais " + personne + ".");
        } else if (commande.startsWith("/unfollow")) {
            String[] args = commande.split(" ");
            String personne = args[1];
            this.utilisateur.removeAbonne(this.server.getUtilisateur(personne));
            this.sendMessageFromServer("Vous ne suivez plus " + personne + ".");
        } else if (commande.startsWith("/quit")) {
            this.utilisateur.setConnecte(false);
            this.client.close();
        } else if (commande.startsWith("/broadcast")) {
            String[] args = commande.split(" ");
            String msg = "";
            for (int i = 1; i < args.length; i++) {
                msg += args[i] + " ";
            }
            this.server.broadcast(this.server.createMessage(msg, this.utilisateur));
            this.server.broadcast(new Message(0, msg, utilisateur));
        } else if (commande.startsWith("/list")) {
            this.afficherUtilisateurs();
        } else {
            this.help();
        }
    }

    public void afficherUtilisateurs() {
        String liste = "Utilisateurs connectés :";
        for (Utilisateur client : this.server.getUtilisateurs()) {
            String personne = client.getPseudo();
            if (client.equals(this.utilisateur)) {personne += " (vous)";}
            else {
                boolean vousSuit = false;
                boolean suivi = false;
                for (Utilisateur abonne : this.utilisateur.getAbonnes()) {
                    if (abonne.equals(client)) {
                        vousSuit = true;
                        break;
                    }
                } if (client.getAbonnes().contains(this.utilisateur)) {suivi = true;}
                if (vousSuit && suivi) {personne += " (amis)";}
                else if (vousSuit) {personne += " (vous suit)";}
                else if (suivi) {personne += " (suivi)";} 
            }
            liste += "\n" + personne;
        } this.sendMessage(liste);
    }

    public void help() {
        String aide = "Commandes disponibles :\n";
        aide += "/name <name> : change le nom du client\n";
        aide += "/msg <name> <message> : envoie un message privé à <name>\n";
        aide += "/follow <name> : suit les messages de <name>\n";
        aide += "/unfollow <name> : ne suit plus les messages de <name>\n";
        aide += "/broadcast <message> : envoie un message à tous les clients\n";
        aide += "/list : affiche les utilisateurs connectés\n";
        aide += "/quit : quitte le serveur\n";
        aide += "/help : affiche les commandes";
        this.sendMessage(aide);
    }

    public void sendMessage(Message message) {
        this.writer.println(message);
    }

    public void sendMessage(String message) {
        Message msg = this.server.createMessage(message, this.utilisateur);
        this.server.addMessage(msg);
        this.writer.println(msg);
    }

    public void sendMessageFromServer(String message) {
        Message msg = this.server.createMessage(message, this.server.getUtilisateurServer());
        this.server.addMessage(msg);
        this.writer.println(msg);
    }

    @Override
    public String toString() {
        return this.utilisateur.getPseudo();
    }
}
