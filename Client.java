import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

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
        // System.out.println(message);
        // parsing du json
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(message, JsonObject.class);
        int id = jsonObject.get("id").getAsInt();
        String contenu = jsonObject.get("contenu").getAsString();
        String expediteur = jsonObject.get("expediteur").getAsString();
        int likes = jsonObject.get("likes").getAsInt();
        int type = jsonObject.get("type").getAsInt();
        String dateString = jsonObject.get("date").getAsString();
        // conversion date String en Date
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
        Date date = new Date();
        try {date = dateFormat.parse(dateString);}
        catch (ParseException e) {e.printStackTrace();}
        // affichage en fonction du type (--A VENIR--)
        System.out.println("id:" + id + " | " + date + " | " + likes + " likes");
        System.out.println(expediteur + ">" + contenu);
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
