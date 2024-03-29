import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Utilisateur {
    
    private int id;
    private String pseudo;
    private String motDePasse;
    private Set<Utilisateur> abonnes;
    private Set<Utilisateur> abonnements;
    private ClientHandler client;
    private Map<Message, Boolean> messages;

    public Utilisateur(int id, String pseudo, String motDePasse) {
        this.id = id;
        this.pseudo = pseudo;
        this.motDePasse = motDePasse;
        this.abonnes = new HashSet<>();
        this.abonnements = new HashSet<>();
        this.messages = new HashMap<>();
        this.client = null;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ClientHandler getClient() {
        return this.client;
    }

    public void setClient(ClientHandler client) {
        this.client = client;
    }

    public String getPseudo() {
        return this.pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getMotDePasse() {
        return this.motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public Set<Utilisateur> getAbonnes() {
        return this.abonnes;
    }
    
    public void addAbonne(Utilisateur utilisateur) {
        if (utilisateur.getPseudo() != this.pseudo) this.abonnes.add(utilisateur);
    }

    public void removeAbonne(Utilisateur utilisateur) {
        this.abonnes.remove(utilisateur);
    }

    public Set<Utilisateur> getAbonnements() {
        return this.abonnements;
    }

    public void addAbonnement(Utilisateur utilisateur) {
        if (utilisateur.getPseudo() != this.pseudo) this.abonnements.add(utilisateur);
    }

    public void removeAbonnement(Utilisateur utilisateur) {
        this.abonnements.remove(utilisateur);
    }

    public Map<Message, Boolean> getMessages() {
        return this.messages;
    }

    public void addMessage(Message message) {
        boolean lu = false;
        if (this.client != null) {lu = true;}
        this.messages.put(message, lu);
        if (this.client != null) {this.client.sendMessage(message);}
    }

    public void removeMessage(Message message) {
        this.messages.remove(message);
    }

    public void setLu(Message message) {
        this.messages.put(message, true);
    }

    public void setNonLu(Message message) {
        this.messages.put(message, false);
    }

    public void setAllLu() {
        for (Message message : this.messages.keySet()) {
            this.messages.put(message, true);
        }
    }

    public void setAllNonLu() {
        for (Message message : this.messages.keySet()) {
            this.messages.put(message, false);
        }
    }

    public int getNombreMessages() {
        return this.messages.keySet().size();
    }

    public Set<Message> getMessagesNonLus() {
        Set<Message> messagesNonLus = new HashSet<>();
        for (Message message : this.messages.keySet()) {
            if (!this.messages.get(message)) {
                messagesNonLus.add(message);
            }
        }
        return messagesNonLus;
    }

    public String sauvegarde() {
        /**
         * Chaine de l'utilisateur pour la sauvegarde
         */
        String utilisateurString = "";
        utilisateurString += this.id + ";";
        utilisateurString += this.pseudo + ";";
        utilisateurString += this.motDePasse + ";";
        for (Utilisateur utilisateur : this.abonnes) {utilisateurString += utilisateur.getId() + ",";}
        if (utilisateurString.endsWith(",")) {utilisateurString = utilisateurString.substring(0, utilisateurString.length() - 1);}
        if (this.abonnes.size() == 0) {utilisateurString += "-1";}
        utilisateurString += ";";
        for (Utilisateur utilisateur : this.abonnements) {utilisateurString += utilisateur.getId() + ",";}
        if (utilisateurString.endsWith(",")) {utilisateurString = utilisateurString.substring(0, utilisateurString.length() - 1);}
        if (this.abonnements.size() == 0) {utilisateurString += "-1";}
        utilisateurString += ";";
        for (Message message : this.messages.keySet()) {utilisateurString += message.getId() + ":" + this.messages.get(message) + ",";}
        if (utilisateurString.endsWith(",")) {utilisateurString = utilisateurString.substring(0, utilisateurString.length() - 1);}
        if (this.messages.keySet().size() == 0) {utilisateurString += "-1";}
        return utilisateurString;
    }

    @Override
    public String toString() {
        return this.pseudo;
    }
}
