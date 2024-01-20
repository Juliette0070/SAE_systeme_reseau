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

    public Utilisateur getUtilisateurServer() {
        return this.getUtilisateur("Serveur");
    }

    public Utilisateur getUtilisateur(String pseudo) {
        for (Utilisateur utilisateur : this.utilisateurs) {
            if (utilisateur.getPseudo().equals(pseudo)) {
                return utilisateur;
            }
        } return null;
    }

    public Utilisateur getUtilisateur(int id) {
        for (Utilisateur utilisateur : this.utilisateurs) {
            if (utilisateur.getId() == id) {
                return utilisateur;
            }
        } return null;
    }

    public void addUtilisateur(Utilisateur utilisateur) {
        this.utilisateurs.add(utilisateur);
    }

    public void broadcast(Message message) {
        for (Utilisateur utilisateur : this.utilisateurs) {
            if (!utilisateur.getPseudo().equals(message.getExpediteur().getPseudo())) {
                utilisateur.addMessage(message);
            }
        } this.messages.add(message);
    }

    public void sendTo(String nomClient, Message message) {
        for (Utilisateur utilisateur : this.utilisateurs) {
            if (utilisateur.getPseudo().equals(nomClient)) {
                utilisateur.addMessage(message);
            }
        } this.messages.add(message);
    }

    public void sendToAbonnes(Message message) {
        for (Utilisateur utilisateur : message.getExpediteur().getAbonnes()) {
            utilisateur.addMessage(message);
        } this.messages.add(message);
    }

    public Message createMessage(String contenu, Utilisateur expediteur, String type) {
        int id = this.messages.size();
        Message message = new Message(id, contenu, expediteur, type);
        return message;
    }

    public void addMessage(Message message) {
        this.messages.add(message);
    }

    // Getters et setters

    public List<Message> getMessages() {
        return this.messages;
    }

    public Set<Utilisateur> getUtilisateurs() {
        return this.utilisateurs;
    }

    public void removeClient(ClientHandler clientHandler) {
        this.utilisateurs.remove(clientHandler.getUtilisateur());
    }

    // Sauvegarde

    public void sauvegarder() {
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
        } catch (Exception e) {System.out.println("erreur lors de la sauvegarde des utilisateurs");}
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
        } catch (Exception e) {System.out.println("erreur lors de la sauvegarde des messages");}
    }

    public void charger() {
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
        } catch (Exception e) {System.out.println("erreur lors du chargement des utilisateurs");}
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
        } catch (Exception e) {System.out.println("erreur lors du chargement des abonnements");}
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
        } catch (Exception e) {System.out.println("erreur lors du chargement des messages");}
        // ajout des messages aux utilisateurs
        for (Message message : listeMessages) {
            for (Utilisateur utilisateur : setUtilisateurs) {
                if (utilisateur.getPseudo().equals(message.getExpediteur().getPseudo())) {
                    utilisateur.addMessage(message);
                }
            }
        }
        // ajout des utilisateurs au serveur
        this.utilisateurs = setUtilisateurs;
        // ajout des messages au serveur
        this.messages = listeMessages;
        if (this.utilisateurs.size() == 0) {
            this.utilisateurs.add(new Utilisateur(0, "Serveur", "admin"));
        }
    }

    // Autres

    public void closeServer() {
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
