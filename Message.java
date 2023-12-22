import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Message {
    
    private int id;
    private String contenu;
    private Utilisateur expediteur;
    private Date date;
    private Set<Utilisateur> likes;

    public Message(int id, String contenu, Utilisateur expediteur) {
        this.id = id;
        this.contenu = contenu;
        this.expediteur = expediteur;
        this.date = new Date();
        this.likes = new HashSet<>();
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContenu() {
        return this.contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public Utilisateur getExpediteur() {
        return this.expediteur;
    }

    public void setExpediteur(Utilisateur expediteur) {
        this.expediteur = expediteur;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Set<Utilisateur> getLikes() {
        return this.likes;
    }
    
    public void addLike(Utilisateur utilisateur) {
        this.likes.add(utilisateur);
    }

    public void removeLike(Utilisateur utilisateur) {
        this.likes.remove(utilisateur);
    }

    public int getNbLikes() {
        return this.likes.size();
    }

    @Override
    public String toString() {
        return "{id=" + this.id +
                ", contenu='" + this.contenu + '\'' +
                ", expediteur='" + this.expediteur + '\'' +
                ", date=" + this.date +
                ", likes=" + this.likes.size() +
                '}';
    }
}
