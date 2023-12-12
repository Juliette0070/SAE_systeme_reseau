import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server implements Runnable {
    
    private ServerSocket serverSocket;
    private BufferedReader reader;
    private List<ClientHandler> clients;

    public Server() throws Exception {
        this.serverSocket = new ServerSocket(4444);
        this.clients = new ArrayList<ClientHandler>();
    }

    @Override
    public void run() {
        try {
            while(true) {
                // System.out.println("Waiting for client...");
                ClientHandler clientHandler = new ClientHandler(serverSocket.accept(),this);
                this.clients.add(clientHandler);
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
