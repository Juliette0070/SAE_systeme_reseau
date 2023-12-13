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
    private List<ClientHandler> clients;
    private Map<ClientHandler, List<ClientHandler>> abonnes;

    public Server() throws Exception {
        this.serverSocket = new ServerSocket(4444);
        this.clients = new ArrayList<ClientHandler>();
        this.abonnes = new HashMap<>();
    }

    @Override
    public void run() {
        try {
            while(true) {
                // System.out.println("Waiting for client...");
                ClientHandler clientHandler = new ClientHandler(serverSocket.accept(),this);
                this.clients.add(clientHandler);
                this.abonnes.put(clientHandler, new ArrayList<>());
                Thread clientThread = new Thread(clientHandler);
                clientThread.start();
            }
        } catch(Exception e) {
            closeServer();
            e.printStackTrace();
        }
    }

    public void broadcast(String message) {
        for(ClientHandler client : this.clients) {
            client.sendMessage(message);
        }
    }

    public void handleMessage(String message, ClientHandler clientHandler){
        for (ClientHandler client : this.clients) {
            client.sendMessage(clientHandler.getName() + ">" + message);
        }
    }

    public void sendTo(String nomClient, String message, ClientHandler clientHandler) {
        for (ClientHandler client : this.clients) {
            if (client.getName().equals(nomClient)) {
                client.sendMessage(clientHandler.getName() + "->You>" + message);
            }
        }
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
        return this.clients.size();
    }

    public static void main(String[] args) throws Exception{
        Server server = new Server();
        server.run();
    }
}
