import java.io.PrintWriter;
import java.util.Date;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Tuito extends Application {

    private Stage primaryStage;
    private Client client;
    private PrintWriter writer;
    private TextArea zoneChat = new TextArea();
    private int etat;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.etat = 0;
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Application de Chat JavaFX");
        try {
            this.client = new Client(this);
            this.writer = new PrintWriter(client.getClientSocket().getOutputStream(), true);
            this.client.start();
        } catch(Exception e) {
            e.printStackTrace();
        }

        // fenetreConnexion();
        fenetreSalon();
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
        // btnConnect.setOnAction(e -> fenetreSalon(userTF.getText())); // set le nom d'utilisateur
        // root.setOnKeyPressed(event -> {
        //     if (event.getCode() == KeyCode.ENTER) { // ici -> vérif réponse du serveur pour la connexion (avant appel à fenetreSalon)
        //         fenetreSalon(userTF.getText());
        //     }
        // });

        root.getChildren().addAll(userLabel, userTF, passwordLabel, passwordPF, btnConnect);

        Scene scene = new Scene(root, 300, 200);
        this.primaryStage.setScene(scene);
        this.primaryStage.show();
    }

    public void handleMessage(int id, Date date, int likes, String expediteur, String contenu, String type) {
        // gérer les différents types de messages (broadcast, privé, normal(abonnés), autre(Server))
        // this.zoneChat.appendText("id:" + id + " | " + date + " | " + likes + " likes\n");
        // this.zoneChat.appendText(expediteur + ": " + contenu + "\n");
        if (type.startsWith("0")) {
            // action pour un message public
            // this.zoneChat.appendText("Message public\n");
            this.zoneChat.appendText("id:" + id + " | " + date + " | " + likes + " likes\n");
            this.zoneChat.appendText(expediteur + "> " + contenu + "\n");
        } else if (type.startsWith("1")) {
            // action pour un message privés
            // this.zoneChat.appendText("Message privé\n");
            this.zoneChat.appendText("id:" + id + " | " + date + " | " + likes + " likes\n");
            this.zoneChat.appendText(expediteur + "->All> " + contenu + "\n");
        } else if (type.startsWith("2")) {
            // action pour un message abonnés
            // this.zoneChat.appendText("Message abonnés\n");
            this.zoneChat.appendText("id:" + id + " | " + date + " | " + likes + " likes\n");
            this.zoneChat.appendText(expediteur + "->You> " + contenu + "\n");
        } else if (type.startsWith("3")) {
            // this.zoneChat.appendText("Message serveur\n");
            if (type.startsWith("30")) {
                // this.zoneChat.appendText("Message de demande\n");
                switch (type) {
                    case "300":
                        // action pour une demande de pseudo
                        this.popUpDemande("Pseudo", "Entrez votre pseudo", contenu);
                        break;
                    case "301":
                        // action pour une demande de mot de passe
                        this.popUpDemande("Mot de passe", "Entrez votre mot de passe", contenu);
                        break;
                    case "302":
                        // action pour une demande de création d'utilisateur
                        this.popUpDemande("Creation de l'utilisateur", "Validation de la creation de l'utilisateur", contenu);
                        break;
                    default:
                        // action par défaut pour un message de demande
                        this.popUpDemande("Message de demande", "Message de demande", contenu);
                        break;
                }
            } else if (type.startsWith("31")) {
                // this.zoneChat.appendText("Message d'information\n");
                switch (type) {
                    case "310":
                        // action pour un utilisateur créé
                        this.popUpInformation("Creation de l'utilisateur", null, contenu);
                        break;
                    case "311":
                        // action pour un utilisateur déjà connecté
                        this.popUpInformation("Utilisateur déjà connecté", null, contenu);
                        break;
                    case "312":
                        // action pour un mot de passe incorrect
                        this.popUpInformation("Mot de passe incorrect", null, contenu);
                        break;
                    case "313":
                        // action pour bienvenue
                        this.zoneChat.appendText(expediteur + "> " + contenu + "\n");
                        break;
                    default:
                        // action par défaut pour un message d'information
                        this.popUpInformation("Message d'information", null, contenu);
                        break;
                }
            } else if (type.startsWith("32")) {
                // this.zoneChat.appendText("Message de retour\n");
                this.zoneChat.appendText(expediteur + "> " + contenu + "\n");
                switch (type) {
                    case "320":
                        // action pour la liste des utilisateurs
                        break;
                    case "321":
                        // action pour la liste des abonnes
                        break;
                    case "322":
                        // action pour la liste des abonnements
                        break;
                    default:
                        // action par défaut pour un message de retour
                        break;
                }
            } else {
                // action par défaut pour un message serveur
            }
        } else if (type.startsWith("4")) {
            // action pour un message de retour utilisateur
            // this.zoneChat.appendText("Message de retour utilisateur\n");
            this.zoneChat.appendText("id:" + id + " | " + contenu + "\n"); // (ne plus afficher le message directement quand on l'envoie mais quand on le reçoit de la part du serveur)
        } else {
            // this.zoneChat.appendText("Message de type inconnu\n");
            this.zoneChat.appendText("id:" + id + " | " + date + " | " + likes + " likes | type:" + type + "\n");
            this.zoneChat.appendText(expediteur + ": " + contenu + "\n");
        }
    }

    public void verifierConnexion(String nomUtilisateur, String motDePasse) {
        // TODO
        this.writer.println(nomUtilisateur);
        this.writer.println(motDePasse);
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Connexion");
        alert.setHeaderText(null);
        alert.setContentText("Connexion reussie !");
        // TODO
        alert.showAndWait();
    }

    public void popUpDemande(String titre, String header, String message) {
        Platform.runLater(() -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle(titre);
            if (header != null) {dialog.setHeaderText(header);}
            dialog.setContentText(message);
            dialog.showAndWait().ifPresent(response -> {
                this.writer.println(response);
            });
        });
    }

    public void popUpInformation(String titre, String header, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle(titre);
            if (header != null) {alert.setHeaderText(header);}
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    private void fenetreSalon() {
        this.primaryStage.setTitle("Salon de Discussion");

        this.zoneChat.setEditable(false);
        this.zoneChat.setWrapText(true);
        this.zoneChat.setPrefHeight(500);
        this.zoneChat.setPrefWidth(700);

        TextField champMessage = new TextField();
        champMessage.setPromptText("Ecrire un message...");
        champMessage.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (champMessage.getText().length() > 0) {
                    envoyerMessage(champMessage.getText());
                    champMessage.clear();
                }
            }
        });

        Button boutonEnvoyer = new Button("Envoyer");
        boutonEnvoyer.setOnAction(e -> {
            if (champMessage.getText().length() > 0) {
                envoyerMessage(champMessage.getText());
                champMessage.clear();
            }
        });

        VBox root = new VBox(10);
        root.getChildren().addAll(zoneChat, champMessage, boutonEnvoyer);

        Scene scene = new Scene(root, 800, 600);
        this.primaryStage.setScene(scene);
        this.primaryStage.show();
    }

    private void envoyerMessage(String message) {
        // String nouveauMessage = "You> " + message + "\n";
        // this.zoneChat.appendText(nouveauMessage);
        this.writer.println(message);
    }
}
