import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server implements Runnable {
    
    private ServerSocket serverSocket;
    private BufferedReader reader;
    private Map<ClientHandler, List<ClientHandler>> abonnes;

    public Server() throws Exception {
        this.serverSocket = new ServerSocket(4444);
        this.abonnes = new HashMap<>();
    }

    @Override
    public void run() {
        try {
            while(true) {
                // System.out.println("Waiting for client...");
                ClientHandler clientHandler = new ClientHandler(serverSocket.accept(),this);
                this.abonnes.put(clientHandler, new ArrayList<>());
                Thread clientThread = new Thread(clientHandler);
                clientThread.start();
            }
        } catch(Exception e) {
            closeServer();
            e.printStackTrace();
        }
    }

    public List<String> getUtilisateurs() {
        List<String> utilisateurs = new ArrayList<>();
        for (ClientHandler client : this.abonnes.keySet()) {
            utilisateurs.add(client.getName());
        }
        return utilisateurs;
    }

    public List<ClientHandler> getAbonnes(ClientHandler clientHandler) {
        List<ClientHandler> abonnes = new ArrayList<>();
        for (ClientHandler client : this.abonnes.get(clientHandler)) {
            abonnes.add(client);
        }
        return abonnes;
    }

    public Map<ClientHandler, List<ClientHandler>> getAbonnes() {
        return this.abonnes;
    }

    public List<ClientHandler> getAbonnes(String nomClient) {
        for (ClientHandler client : this.abonnes.keySet()) {
            if (client.getName().equals(nomClient)) {
                return this.abonnes.get(client);
            }
        }
        return null;
    }

    public void broadcast(String message, ClientHandler clientHandler){
        for (ClientHandler client : this.abonnes.keySet()) {
            if (!client.getName().equals(clientHandler.getName())) {
                client.sendMessage(clientHandler.getName() + ">" + message);
            }
        }
    }

    public void sendTo(String nomClient, String message, ClientHandler clientHandler) {
        for (ClientHandler client : this.abonnes.keySet()) {
            if (client.getName().equals(nomClient)) {
                client.sendMessage(clientHandler.getName() + "->You>" + message);
            }
        }
    }

    public void sendToAbonnes(String message, ClientHandler clientHandler) {
        for (ClientHandler client : this.abonnes.get(clientHandler)) {
            client.sendMessage(clientHandler.getName() + ">" + message);
        }
    }

    public void addAbonne(String nomClient, ClientHandler clientHandler) {
        if (!nomClient.equals(clientHandler.getName())) {
            for (ClientHandler client : this.abonnes.keySet()) {
                if (client.getName().equals(nomClient)) {
                    this.abonnes.get(client).add(clientHandler);
                }
            }
        }
    }

    public void removeAbonne(String nomClient, ClientHandler clientHandler) {
        for (ClientHandler client : this.abonnes.keySet()) {
            if (client.getName().equals(nomClient)) {
                this.abonnes.get(client).remove(clientHandler);
            }
        }
    }

    public void removeClient(ClientHandler clientHandler) {
        this.abonnes.remove(clientHandler);
    }

    public boolean nameAlreadyUsed(String name) {
        for (ClientHandler client : this.abonnes.keySet()) {
            if (client.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    private void closeServer() {
        System.out.println("Closing server...");
        try {
            this.serverSocket.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public int getNombreConnectes(){
        return this.abonnes.keySet().size();
    }

    public static void main(String[] args) throws Exception{
        Server server = new Server();
        server.run();
    }
}
