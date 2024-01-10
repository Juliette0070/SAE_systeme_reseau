import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client implements Runnable {
    
    private Socket clientSocket;
    private BufferedReader reader;
    private PrintWriter writer;

    public Client() throws Exception {
        this.clientSocket = new Socket("localhost", 4444); // Pour se connecter en local
        // this.clientSocket = new Socket("0.tcp.eu.ngrok.io", 17204); // Pour se connecter au i
        this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.writer = new PrintWriter(clientSocket.getOutputStream(), true);
    }

    @Override
    public void run() {
        try {
            InputHandler inputHandler = new InputHandler(this.clientSocket);
            Thread inputThread = new Thread(inputHandler);
            inputThread.start();

            String message;
            while ((message = reader.readLine()) != null) {
                this.afficheMessage(message);
            }
        } catch (Exception e) {
            closeSocket();
            e.printStackTrace();
        }
    }

    public void afficheMessage(String message) {
        System.out.println(message);
    }

    private void closeSocket() {
        try {
            this.clientSocket.close();
            this.reader.close();
            this.writer.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        Client client = new Client();
        client.run();
    }
}
