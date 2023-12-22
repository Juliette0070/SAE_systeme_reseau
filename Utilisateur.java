import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Utilisateur {
    
    private String pseudo;
    private String motDePasse;
    private List<Utilisateur> abonnes;
    private boolean connecte;
    private Map<Message, Boolean> messages;

    public Utilisateur(String pseudo, String motDePasse) {
        this.pseudo = pseudo;
        this.motDePasse = motDePasse;
        this.abonnes = new ArrayList<>();
        this.connecte = true;
        this.messages = new HashMap<>();
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

    public List<Utilisateur> getAbonnes() {
        return this.abonnes;
    }
    
    public void addAbonne(Utilisateur utilisateur) {
        this.abonnes.add(utilisateur);
    }

    public void removeAbonne(Utilisateur utilisateur) {
        this.abonnes.remove(utilisateur);
    }

    public boolean isConnecte() {
        return this.connecte;
    }

    public void setConnecte(boolean connecte) {
        this.connecte = connecte;
    }

    public Map<Message, Boolean> getMessages() {
        return this.messages;
    }

    public void addMessage(Message message) {
        boolean lu = false;
        if (this.connecte) {lu = true;}
        this.messages.put(message, lu);
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
}
