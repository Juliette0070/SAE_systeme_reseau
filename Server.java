import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.ServerSocket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class Server implements Runnable {
    
    private ServerSocket serverSocket;
    private List<Message> messages;
    private Set<Utilisateur> utilisateurs;

    public Server() throws Exception {
        this.serverSocket = new ServerSocket(4444);
        this.messages = new ArrayList<>();
        this.utilisateurs = new HashSet<>();
        this.utilisateurs.add(new Utilisateur(0, "Serveur", "admin"));
        this.charger();
    }

    @Override
    public void run() {
        System.out.println("Server running on " + this.serverSocket.getInetAddress() + ":" + this.serverSocket.getLocalPort());
        try {
            while(true) {
                ClientHandler clientHandler = new ClientHandler(serverSocket.accept(),this);
                Thread clientThread = new Thread(clientHandler);
                clientThread.start();
            }
        } catch(Exception e) {
            closeServer();
            e.printStackTrace();
        }
    }

    // Getters et Setters + Adders et Removers

    public Utilisateur getUtilisateurServer() {
        /**
         * Renvoie l'utilisateur Serveur
         */
        return this.getUtilisateur("Serveur");
    }

    public Utilisateur getUtilisateur(String pseudo) {
        /**
         * Renvoie l'utilisateur avec le pseudo donné
         * @param pseudo le pseudo de l'utilisateur
         */
        for (Utilisateur utilisateur : this.utilisateurs) {
            if (utilisateur.getPseudo().equals(pseudo)) {
                return utilisateur;
            }
        } return null;
    }

    public Utilisateur getUtilisateur(int id) {
        /**
         * Renvoie l'utilisateur avec l'id donné
         * @param id l'id de l'utilisateur
         */
        for (Utilisateur utilisateur : this.utilisateurs) {
            if (utilisateur.getId() == id) {
                return utilisateur;
            }
        } return null;
    }

    public void addUtilisateur(Utilisateur utilisateur) {
        /**
         * Ajoute un utilisateur à la liste des utilisateurs
         * @param utilisateur l'utilisateur à ajouter
         */
        this.utilisateurs.add(utilisateur);
    }

    public void addMessage(Message message) {
        /**
         * Ajoute un message à la liste des messages
         * @param message le message à ajouter
         */
        this.messages.add(message);
    }

    public List<Message> getMessages() {
        /**
         * Renvoie la liste des messages
         */
        return this.messages;
    }

    public Set<Utilisateur> getUtilisateurs() {
        /**
         * Renvoie la liste des utilisateurs
         */
        return this.utilisateurs;
    }

    public void removeClient(ClientHandler clientHandler) {
        /**
         * Supprime un client de la liste des utilisateurs
         * @param clientHandler le client à supprimer
         */
        this.utilisateurs.remove(clientHandler.getUtilisateur());
    }

    public Message getMessage(int id) {
        /**
         * Renvoie le message avec l'id donné
         * @param id l'id du message
         */
        for (Message message : this.messages) {
            if (message.getId() == id) {
                return message;
            }
        } return null;
    }

    public void removeUtilisateur(Utilisateur utilisateur) {
        /**
         * Supprime un utilisateur de la liste des utilisateurs
         * @param utilisateur l'utilisateur à supprimer
         */
        this.utilisateurs.remove(utilisateur);
    }

    public int getIdUtilisateur() {
        /**
         * Renvoie un id utilisateur non utilisé
         */
        int id = this.utilisateurs.size();
        while (this.getUtilisateur(id) != null) {
            id++;
        } return id;
    }

    // Messages

    public void broadcast(Message message) {
        /**
         * Envoie un message à tous les utilisateurs
         * @param message le message à envoyer
         */
        for (Utilisateur utilisateur : this.utilisateurs) {
            if (!utilisateur.getPseudo().equals(message.getExpediteur().getPseudo())) {
                utilisateur.addMessage(message);
            }
        } this.messages.add(message);
    }

    public void sendTo(String nomClient, Message message) {
        /**
         * Envoie un message à un utilisateur
         * @param nomClient le pseudo de l'utilisateur
         * @param message le message à envoyer
         */
        for (Utilisateur utilisateur : this.utilisateurs) {
            if (utilisateur.getPseudo().equals(nomClient)) {
                utilisateur.addMessage(message);
            }
        } this.messages.add(message);
    }

    public void sendToAbonnes(Message message) {
        /**
         * Envoie un message à tous les abonnés de l'expéditeur
         * @param message le message à envoyer
         */
        for (Utilisateur utilisateur : message.getExpediteur().getAbonnes()) {
            utilisateur.addMessage(message);
        } this.messages.add(message);
    }

    public Message createMessage(String contenu, Utilisateur expediteur, String type) {
        /**
         * Crée un message
         * @param contenu le contenu du message
         * @param expediteur l'expéditeur du message
         * @param type le type du message
         */
        int id = this.messages.size();
        Message message = new Message(id, contenu, expediteur, type);
        return message;
    }

    // Sauvegarde

    public void sauvegarder() {
        /**
         * Sauvegarde les utilisateurs et les messages dans des fichiers
         */
        // fichier utilisateurs
        String chaineUtilisateurs = "";
        for (Utilisateur utilisateur : this.utilisateurs) {chaineUtilisateurs += utilisateur.sauvegarde() + "\n";}
        File fichierUtilisateurs = new File("sauvegardes/utilisateurs.txt");
        if (!fichierUtilisateurs.getParentFile().exists()) {fichierUtilisateurs.getParentFile().mkdirs();}
        try {
            FileWriter fileWriterUtilisateurs = new FileWriter(fichierUtilisateurs);
            BufferedWriter bufferedWriterUtilisateurs = new BufferedWriter(fileWriterUtilisateurs);
            bufferedWriterUtilisateurs.write(chaineUtilisateurs);
            bufferedWriterUtilisateurs.close();
            fileWriterUtilisateurs.close();
        } catch (Exception e) {System.out.println("erreur lors de la sauvegarde des utilisateurs ou fichier vide/inexistant");}
        // fichier messages
        String chaineMessages = "";
        for (Message message : this.messages) {chaineMessages += message.sauvegarde() + "\n";}
        File fichierMessages = new File("sauvegardes/messages.txt");
        if (!fichierMessages.getParentFile().exists()) {fichierMessages.getParentFile().mkdirs();}
        try {
            FileWriter fileWriterMessages = new FileWriter(fichierMessages);
            BufferedWriter bufferedWriterMessages = new BufferedWriter(fileWriterMessages);
            bufferedWriterMessages.write(chaineMessages);
            bufferedWriterMessages.close();
            fileWriterMessages.close();
        } catch (Exception e) {System.out.println("erreur lors de la sauvegarde des messages ou fichier vide/inexistant");}
    }

    public void charger() {
        /**
         * Charge les utilisateurs et les messages depuis des fichiers
         */
        Set<Utilisateur> setUtilisateurs = new HashSet<>();
        List<Message> listeMessages = new ArrayList<>();
        // fichier utilisateurs
        try {
            File fichierUtilisateurs = new File("sauvegardes/utilisateurs.txt");
            java.util.Scanner scannerUtilisateurs = new java.util.Scanner(fichierUtilisateurs);
            while (scannerUtilisateurs.hasNextLine()) {
                String ligne = scannerUtilisateurs.nextLine();
                String[] elements = ligne.split(";");
                int id = Integer.parseInt(elements[0]);
                String pseudo = elements[1];
                String motDePasse = elements[2];
                Utilisateur utilisateur = new Utilisateur(id, pseudo, motDePasse);
                setUtilisateurs.add(utilisateur);
            } scannerUtilisateurs.close();
        } catch (Exception e) {System.out.println("erreur lors du chargement des utilisateurs ou fichier vide/inexistant");}
        // ajout des abonnements
        try {
            File fichierUtilisateurs = new File("sauvegardes/utilisateurs.txt");
            java.util.Scanner scannerUtilisateurs = new java.util.Scanner(fichierUtilisateurs);
            while (scannerUtilisateurs.hasNextLine()) {
                String ligne = scannerUtilisateurs.nextLine();
                String[] elements = ligne.split(";");
                int id = Integer.parseInt(elements[0]);
                String[] abonnesString = new String[0];
                if (!elements[3].equals("-1")) {abonnesString = elements[3].split(",");}
                String[] abonnementsString = new String[0];
                if (!elements[4].equals("-1")) {abonnementsString = elements[4].split(",");}
                Utilisateur uti = null;
                for (Utilisateur utilisateur : setUtilisateurs) { // récupération de l'utilisateur
                    if (utilisateur.getId() == id) {
                        uti = utilisateur;
                        break;
                    }
                }
                for (Utilisateur utilisateur : setUtilisateurs) {
                    for (String abonneString : abonnesString) { // ajout des abonnés
                        if (utilisateur.getId() == Integer.parseInt(abonneString)) {
                            uti.addAbonne(utilisateur);
                        }
                    }
                    for (String abonnementString : abonnementsString) { // ajout des abonnements
                        if (utilisateur.getId() == Integer.parseInt(abonnementString)) {
                            uti.addAbonnement(utilisateur);
                        }
                    }
                }
            } scannerUtilisateurs.close();
        } catch (Exception e) {System.out.println("erreur lors du chargement des abonnements ou fichier vide/inexistant");}
        // fichier messages
        try {
            File fichierMessages = new File("sauvegardes/messages.txt");
            java.util.Scanner scannerMessages = new java.util.Scanner(fichierMessages);
            while (scannerMessages.hasNextLine()) {
                String ligne = scannerMessages.nextLine();
                String[] elements = ligne.split(";");
                int id = Integer.parseInt(elements[0]);
                String contenu = elements[1];
                int expediteurInt = Integer.parseInt(elements[2]);
                Utilisateur expediteur = null;
                for (Utilisateur utilisateur : setUtilisateurs) {
                    if (utilisateur.getId() == expediteurInt) {expediteur = utilisateur;}
                }
                String dateString = elements[3];
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
                Date date = new Date();
                try {date = dateFormat.parse(dateString);}
                catch (ParseException e) {e.printStackTrace();}
                String type = elements[6];
                Message message = new Message(id, contenu, expediteur, type);
                message.setDate(date);
                String likes = elements[4];
                String[] likesString = new String[0];
                if (!likes.equals("-1")) {likesString = likes.split(",");}
                for (String like : likesString) {
                    for (Utilisateur utilisateur : setUtilisateurs) {
                        if (utilisateur.getId() == Integer.parseInt(like)) {
                            message.addLike(utilisateur);
                        }
                    }
                } if (elements[5].equals("true")) {message.supprime(true);}
                listeMessages.add(message);
            } scannerMessages.close();
        } catch (Exception e) {System.out.println("erreur lors du chargement des messages ou fichier vide/inexistant");}
        // ajout des messages aux utilisateurs
        try {
            for (Message message : listeMessages) {
                for (Utilisateur utilisateur : setUtilisateurs) {
                    if (utilisateur.getPseudo().equals(message.getExpediteur().getPseudo())) {
                        utilisateur.addMessage(message);
                        utilisateur.setLu(message);
                    }
                }
            }
        } catch (Exception e) {System.out.println("erreur lors du chargement des messages des utilisateurs ou fichier vide/inexistant");}
        // ajout des utilisateurs au serveur
        this.utilisateurs = setUtilisateurs;
        // ajout des messages au serveur
        this.messages = listeMessages;
        // ajouter le Serveur s'il n'existe pas
        if (this.utilisateurs.size() == 0) {
            this.utilisateurs.add(new Utilisateur(0, "Serveur", "admin"));
        }
    }

    // Autres

    public void closeServer() {
        /**
         * Ferme le serveur
         */
        System.out.println("Saving server...");
        this.sauvegarder();
        System.out.println("Closing server...");
        try {
            this.serverSocket.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception{
        Server server = new Server();
        server.run();
    }
}
