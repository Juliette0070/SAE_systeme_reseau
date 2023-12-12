import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class InputHandler implements Runnable {

    private Socket clientSocket;

    public InputHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
            while (true) {
                String message = reader.readLine();
                writer.println(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
