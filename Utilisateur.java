import java.util.ArrayList;
import java.util.List;

public class Utilisateur {
    
    private String pseudo;
    private String motDePasse;
    private List<Utilisateur> abonnements;

    public Utilisateur(String pseudo, String motDePasse) {
        this.pseudo = pseudo;
        this.motDePasse = motDePasse;
        this.abonnements = new ArrayList<>();
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

    public List<Utilisateur> getAbonnements() {
        return this.abonnements;
    }
    
    public void addAbonnement(Utilisateur utilisateur) {
        this.abonnements.add(utilisateur);
    }

    public void removeAbonnement(Utilisateur utilisateur) {
        this.abonnements.remove(utilisateur);
    }
}
