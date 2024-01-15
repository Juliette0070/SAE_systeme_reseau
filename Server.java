import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
 * à faire:
 * vérifier si l'utilisateur existe déjà
 * - si oui, vérifier si le mot de passe est correct et qu'il n'est pas déjà connecté
 * - si non, créer l'utilisateur
 * ajuster les autres méthodes en conséquence
 */

public class Server implements Runnable {
    
    private ServerSocket serverSocket;
    private BufferedReader reader;
    private List<Message> messages;
    private Set<Utilisateur> utilisateurs;

    public Server() throws Exception {
        this.serverSocket = new ServerSocket(4444);
        this.messages = new ArrayList<>();
        this.utilisateurs = new HashSet<>();
        this.utilisateurs.add(new Utilisateur("Serveur", "admin"));
    }

    @Override
    public void run() {
        System.out.println("Server running on port 4444");
        try {
            while(true) {
                // System.out.println("Waiting for client...");
                ClientHandler clientHandler = new ClientHandler(serverSocket.accept(),this);
                Thread clientThread = new Thread(clientHandler);
                clientThread.start();
            }
        } catch(Exception e) {
            closeServer();
            e.printStackTrace();
        }
    }

    // Tests

    public Utilisateur getUtilisateurServer() {
        return this.getUtilisateur("Serveur");
    }

    public Utilisateur getUtilisateur(String pseudo) {
        for (Utilisateur utilisateur : this.utilisateurs) {
            if (utilisateur.getPseudo().equals(pseudo)) {
                return utilisateur;
            }
        }
        return null;
    }

    public void addUtilisateur(Utilisateur utilisateur) {
        this.utilisateurs.add(utilisateur);
    }

    public void test(Message message) {
        System.out.println("ID:" + message.getId() + " | " + message.getDate() + " | " + message.getLikes().size() + "likes");
        System.out.println(message.getExpediteur() + "> " + message.getContenu());
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

    public Message createMessage(String contenu, Utilisateur expediteur) {
        int id = this.messages.size();
        Message message = new Message(id, contenu, expediteur);
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

    // Autres

    private void closeServer() {
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
