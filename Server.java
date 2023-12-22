import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private Map<ClientHandler, List<ClientHandler>> clients; // ancienne version à supprimer mais j'ai pas encore converti le reste
    private List<Message> messages;
    private List<Utilisateur> utilisateurs;

    public Server() throws Exception {
        this.serverSocket = new ServerSocket(4444);
        this.clients = new HashMap<>();
        this.messages = new ArrayList<>();
        this.utilisateurs = new ArrayList<>();
    }

    @Override
    public void run() {
        try {
            while(true) {
                // System.out.println("Waiting for client...");
                ClientHandler clientHandler = new ClientHandler(serverSocket.accept(),this);
                this.clients.put(clientHandler, new ArrayList<>());
                Thread clientThread = new Thread(clientHandler);
                clientThread.start();
            }
        } catch(Exception e) {
            closeServer();
            e.printStackTrace();
        }
    }

    // Tests

    public void addMessage(String contenu, Utilisateur expediteur) {
        synchronized(this.messages) {
            this.messages.add(new Message(this.messages.size()+1, contenu, expediteur));
        }
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

    public void test() {
        System.out.println("ID:000 | 20/01/2024-09:00 | 3♥");
        System.out.println("toto> Hello world !");
    }

    public void test(Message message) {
        System.out.println("ID:" + message.getId() + " | " + message.getDate() + " | " + message.getLikes().size() + "likes");
        System.out.println(message.getExpediteur() + "> " + message.getContenu());
    }
    
    // Gestion des messages

    public void handleMessage(String message, ClientHandler clientHandler) {

    }

    // messages

    public void broadcast(String message, ClientHandler clientHandler){
        for (ClientHandler client : this.clients.keySet()) {
            if (!client.getName().equals(clientHandler.getName())) {
                client.sendMessage(clientHandler.getName() + ">" + message);
            }
        }
    }

    public void sendTo(String nomClient, String message, ClientHandler clientHandler) {
        for (ClientHandler client : this.clients.keySet()) {
            if (client.getName().equals(nomClient)) {
                client.sendMessage(clientHandler.getName() + "->You>" + message);
            }
        }
    }

    public void sendToAbonnes(String message, ClientHandler clientHandler) {
        for (ClientHandler client : this.clients.get(clientHandler)) {
            client.sendMessage(clientHandler.getName() + ">" + message);
        }
    }

    // Getters et setters

    public List<String> getUtilisateurs() {
        List<String> utilisateurs = new ArrayList<>();
        for (ClientHandler client : this.clients.keySet()) {
            utilisateurs.add(client.getName());
        }
        return utilisateurs;
    }

    public List<ClientHandler> getAbonnes(ClientHandler clientHandler) {
        List<ClientHandler> abonnes = new ArrayList<>();
        for (ClientHandler client : this.clients.get(clientHandler)) {
            abonnes.add(client);
        }
        return abonnes;
    }

    public Map<ClientHandler, List<ClientHandler>> getAbonnes() {
        return this.clients;
    }

    public List<ClientHandler> getAbonnes(String nomClient) {
        for (ClientHandler client : this.clients.keySet()) {
            if (client.getName().equals(nomClient)) {
                return this.clients.get(client);
            }
        }
        return null;
    }

    public void addAbonne(String nomClient, ClientHandler clientHandler) {
        if (!nomClient.equals(clientHandler.getName())) {
            for (ClientHandler client : this.clients.keySet()) {
                if (client.getName().equals(nomClient)) {
                    this.clients.get(client).add(clientHandler);
                }
            }
        }
    }

    public void removeAbonne(String nomClient, ClientHandler clientHandler) {
        for (ClientHandler client : this.clients.keySet()) {
            if (client.getName().equals(nomClient)) {
                this.clients.get(client).remove(clientHandler);
            }
        }
    }

    public void removeClient(ClientHandler clientHandler) {
        this.clients.remove(clientHandler);
    }

    public int getNombreConnectes(){
        return this.clients.keySet().size();
    }

    public boolean nameAlreadyUsed(String name) {
        for (ClientHandler client : this.clients.keySet()) {
            if (client.getName().equals(name)) {
                return true;
            }
        }
        return false;
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
