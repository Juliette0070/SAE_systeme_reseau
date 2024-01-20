import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
                try {this.recupererUtilisateur();}catch (IOException e) {e.printStackTrace();}
            }
            this.utilisateur.setConnecte(true);
            this.utilisateur.setClient(this);
            this.sendMessageFromServer("Bienvenue " + this.utilisateur.getPseudo() + " !", "313");
            this.afficherMessagesNonLus();
            while (this.utilisateur.isConnecte()) {
                String message = reader.readLine();
                if (message == null || message.equals("")) {continue;}
                if (message.startsWith("/")) {this.handleCommande(message);}
                else {
                    this.sendMessage(this.server.createMessage(message, this.utilisateur, "4"));
                    this.server.sendToAbonnes(this.server.createMessage(message, this.utilisateur, "0"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(this.client.getInetAddress() + " encountered an error.");
        }
        this.utilisateur.setConnecte(false);
        this.utilisateur.setClient(null);
        System.out.println(this.client.getInetAddress() + " disconnected.");
    }

    public Utilisateur getUtilisateur() {
        return this.utilisateur;
    }

    public void recupererUtilisateur() throws IOException {
        this.sendMessageFromServer("Pseudo:", "300");
        String pseudo = this.reader.readLine();
        this.sendMessageFromServer("Mot de passe:", "301");
        String motDePasse = this.reader.readLine();
        this.utilisateur = this.server.getUtilisateur(pseudo);
        if (this.utilisateur == null) {
            this.sendMessageFromServer("Cet utilisateur n'existe pas, voulez-vous le creer ? (o/n)", "302");
            String reponse = this.reader.readLine();
            if (reponse.equals("o") || reponse.equals("oui")) {
                this.utilisateur = new Utilisateur(this.server.getUtilisateurs().size(), pseudo, motDePasse);
                this.server.addUtilisateur(this.utilisateur);
                this.sendMessageFromServer("Utilisateur cree !", "310");
            }
        } else if (this.utilisateur.isConnecte()) {
            this.utilisateur = null;
            this.sendMessageFromServer("Cet utilisateur est deja connecte !", "311");
        } else if (!this.utilisateur.getMotDePasse().equals(motDePasse)) {
            this.utilisateur = null;
            this.sendMessageFromServer("Mot de passe incorrect !", "312");
        }
    }

    public void handleCommande(String commande) throws IOException {
        if (commande.startsWith("/name")) {
            String nom = commande.split(" ")[1];
            if (this.server.getUtilisateur(nom) == null) {
                this.utilisateur.setPseudo(nom);
            } else {
                this.sendMessageFromServer("Ce pseudo est deja utilise !", "314");
            }
        } else if (commande.startsWith("/msg")) {
            String[] args = commande.split(" ");
            String personne = args[1];
            String msg = "";
            for (int i = 2; i < args.length; i++) {msg += args[i] + " ";}
            this.sendMessage(this.server.createMessage(msg, this.utilisateur, "4"));
            this.server.sendTo(personne, this.server.createMessage(msg, this.utilisateur, "2"));
        } else if (commande.startsWith("/follow")) {
            String[] args = commande.split(" ");
            String personne = args[1];
            Utilisateur user = this.server.getUtilisateur(personne);
            if (user != null) {
                user.addAbonne(this.utilisateur);
                this.utilisateur.addAbonnement(user);
                this.sendMessageFromServer("Vous suivez desormais " + personne + ".", "315");
            } else {
                this.sendMessageFromServer("L'utilisateur " + personne + " n'existe pas.", "316");
            }
        } else if (commande.startsWith("/unfollow")) {
            String[] args = commande.split(" ");
            String personne = args[1];
            Utilisateur user = this.server.getUtilisateur(personne);
            if (user != null && this.utilisateur.getAbonnements().contains(user)) {
                user.removeAbonne(this.utilisateur);
                this.utilisateur.removeAbonnement(user);
                this.sendMessageFromServer("Vous ne suivez plus " + personne + ".", "317");
            } else {
                this.sendMessageFromServer("L'utilisateur " + personne + " n'existe pas ou n'est pas dans votre liste d'abonnements", "318");
            }
        } else if (commande.startsWith("/abonnes")) {
            Integer nbFollowers = this.utilisateur.getAbonnes().size();
            if (nbFollowers > 0) {
                String followers = "";
                for (Utilisateur user : this.utilisateur.getAbonnes()) {
                    followers += user.getPseudo() + ",";
                } if (followers.endsWith(",")) {followers = followers.substring(0, followers.length() - 2);}
                this.sendMessageFromServer(followers, "321");
            }
        } else if (commande.startsWith("/suivi")) {
            Integer nbSuivis = this.utilisateur.getAbonnements().size();
            if (nbSuivis > 0) {
                String abonnements = "";
                for (Utilisateur user : this.utilisateur.getAbonnements()) {
                    abonnements += user.getPseudo() + ",";
                } if (abonnements.endsWith(",")) {abonnements = abonnements.substring(0, abonnements.length() - 2);}
                this.sendMessageFromServer(abonnements, "322");
            }
        } else if (commande.startsWith("/quit")) {
            this.utilisateur.setConnecte(false);
            this.utilisateur.setClient(null);
            this.client.close();
        } else if (commande.startsWith("/broadcast")) {
            String[] args = commande.split(" ");
            String msg = "";
            for (int i = 1; i < args.length; i++) {
                msg += args[i] + " ";
            }
            this.sendMessage(this.server.createMessage(msg, this.utilisateur, "4"));
            this.server.broadcast(this.server.createMessage(msg, this.utilisateur, "1"));
        } else if (commande.startsWith("/list")) {
            this.afficherUtilisateurs();
        }else if (commande.startsWith("/like")) {
            // liker
        } else if (commande.startsWith("/unlike")) {
            // unliker
        } else if (commande.startsWith("/delete")) {
            if (this.utilisateur.getPseudo().equals("Serveur")) {
                // supprimer le message peut importe l'utilisateur
            } else {
                // supprimer le message s'il appartient à l'utilisateur
            }
        } else if (commande.startsWith("/remove")) {
            if (this.utilisateur.getPseudo().equals("Serveur")) {
                // supprimer l'utilisateur
            }
        } else if (commande.startsWith("/save")) {
            if (this.utilisateur.getPseudo().equals("Serveur")) {
                this.server.sauvegarder();
            }
        } else {
            this.help();
        }
    }

    public void afficherUtilisateurs() {
        String liste = "";
        for (Utilisateur client : this.server.getUtilisateurs()) {
            String personne = client.getPseudo();
            if (!client.getPseudo().equals("Serveur")) {
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
                } liste += personne + ", ";
            }
        } if (liste.endsWith(", ")) {liste = liste.substring(0, liste.length() - 2);}
        this.sendMessageFromServer(liste, "320");
    }

    public void help() {
        String aide = "";
        aide += "/name <name> : change le nom du client,";
        aide += "/msg <name> <message> : envoie un message prive a <name>,";
        aide += "/follow <name> : suit les messages de <name>,";
        aide += "/unfollow <name> : ne suit plus les messages de <name>,";
        aide += "/like <id_message> : like le message dont l'id est <id_message> --A VENIR--,";
        aide += "/unlike <id_message> : enleve le like sur le message dont l'id est <id_message> (si like) --A VENIR--,";
        aide += "/delete <id_message> : supprime le message dont l'id est <id_message> (si c'est vous qui l'avez poste) --A VENIR--,";
        aide += "/broadcast <message> : envoie un message a tous les utilisateurs,";
        aide += "/list : affiche les utilisateurs connectes,";
        aide += "/quit : quitte le serveur,";
        aide += "/help : affiche les commandes";
        this.sendMessageFromServer(aide, "323");
    }

    public void sendMessage(Message message) {
        this.writer.println(message);
    }

    public void sendMessageFromServer(String message) {
        Message msg = this.server.createMessage(message, this.server.getUtilisateurServer(), "3");
        this.server.addMessage(msg);
        this.writer.println(msg);
    }

    public void sendMessageFromServer(String message, String type) {
        Message msg = this.server.createMessage(message, this.server.getUtilisateurServer(), type);
        this.server.addMessage(msg);
        this.writer.println(msg);
    }

    public void afficherMessagesNonLus() {
        for (Message message : this.utilisateur.getMessagesNonLus()) {
            this.sendMessage(message);
            this.utilisateur.setLu(message);
        }
    }

    @Override
    public String toString() {
        return this.utilisateur.getPseudo();
    }
}
