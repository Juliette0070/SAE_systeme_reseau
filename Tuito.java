import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Tuito extends Application {

    private Stage primaryStage;
    private Client client;

    private TextArea zoneChat = new TextArea();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Application de Chat JavaFX");
        try {
            this.client = new Client();
        } catch(Exception e) {
            e.printStackTrace();
        }

        fenetreConnexion();
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
            if (event.getCode() == KeyCode.ENTER) {
                fenetreSalon(userTF.getText());
            }
        });

        root.getChildren().addAll(userLabel, userTF, passwordLabel, passwordPF, btnConnect);

        Scene scene = new Scene(root, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void fenetreSalon(String nomUtilisateur) {
        primaryStage.setTitle("Salon de Discussion - " + nomUtilisateur);

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
        primaryStage.setScene(scene);
    }

    private void envoyerMessage(String expediteur, String message) {

        
        String nouveauMessage = expediteur + ": " + message + "\n";
        this.zoneChat.appendText(nouveauMessage);
    }
}
