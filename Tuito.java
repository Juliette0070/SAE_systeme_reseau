import java.io.PrintWriter;
import java.util.Date;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Tuito extends Application {

    private Stage primaryStage;
    private Client client;
    private PrintWriter writer;
    private TextArea zoneChat = new TextArea();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Application de Chat JavaFX");
        try {
            this.client = new Client(this);
            this.writer = new PrintWriter(client.getClientSocket().getOutputStream(), true);
        } catch(Exception e) {
            e.printStackTrace();
        }

        // fenetreConnexion();
        fenetreSalon("Test");
    }

    private void fenetreConnexion() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        Label userLabel = new Label("Nom d'utilisateur:");
        TextField userTF = new TextField();

        Label passwordLabel = new Label("Mot de passe:");
        PasswordField passwordPF = new PasswordField();

        Button btnConnect = new Button("Se connecter");

        // Détection de connexion
        btnConnect.setOnAction(e -> fenetreSalon(userTF.getText()));
        root.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) { // ici -> vérif réponse du serveur pour la connexion (avant appel à fenetreSalon)
                fenetreSalon(userTF.getText());
            }
        });

        root.getChildren().addAll(userLabel, userTF, passwordLabel, passwordPF, btnConnect);

        Scene scene = new Scene(root, 300, 200);
        this.primaryStage.setScene(scene);
        this.primaryStage.show();
    }

    public void handleMessage(int id, Date date, int likes, String expediteur, String contenu, int type) {
        this.zoneChat.appendText(contenu);
    }

    public void verifierConnexion(String nomUtilisateur, String motDePasse) {
        this.writer.println(nomUtilisateur); System.out.println(nomUtilisateur);
        this.writer.println(motDePasse); System.out.println(motDePasse);
        // TODO
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Connexion");
        alert.setHeaderText(null);
        alert.setContentText("Connexion réussie !");
        alert.showAndWait();
    }

    private void fenetreSalon(String nomUtilisateur) {
        this.primaryStage.setTitle("Salon de Discussion - " + nomUtilisateur);

        this.zoneChat.setEditable(false);
        this.zoneChat.setWrapText(true);

        TextField champMessage = new TextField();
        champMessage.setPromptText("Écrire un message...");
        champMessage.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (champMessage.getText().length() > 0) {
                    envoyerMessage(nomUtilisateur, champMessage.getText());
                    champMessage.clear();
                }
            }
        });

        Button boutonEnvoyer = new Button("Envoyer");
        boutonEnvoyer.setOnAction(e -> {
            if (champMessage.getText().length() > 0) {
                envoyerMessage(nomUtilisateur, champMessage.getText());
                champMessage.clear();
            }
        });

        VBox root = new VBox(10);
        root.getChildren().addAll(zoneChat, champMessage, boutonEnvoyer);

        Scene scene = new Scene(root, 800, 600);
        this.primaryStage.setScene(scene);
        this.primaryStage.show();
    }

    private void envoyerMessage(String expediteur, String message) {

        String nouveauMessage = expediteur + ": " + message + "\n";
        this.zoneChat.appendText(nouveauMessage);
        this.writer.println(message); System.out.println(message);
    }
}
