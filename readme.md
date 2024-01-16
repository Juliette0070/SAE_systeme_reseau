# SAE réseau/système

## Juliette ROUSSELET

## Jordan LAVENANT

## Création d'une appli de messagerie

### commande pour compiler (linux puis windows)

javac -d bin -cp .:lib/gson-2.10.1.jar --module-path /usr/share/openjfx/lib --add-modules javafx.controls *.java
javac -d bin -cp ".;lib/gson-2.10.1.jar" --module-path c:\\Users\\Utilisateur\\Desktop\\TéléchargementBis\\openjfx\\javafx-sdk-20.0.1\\lib\\ --add-modules javafx.controls *.java

### commande pour executer

linux :

java -cp .:bin:lib/gson-2.10.1.jar --module-path /usr/share/openjfx/lib --add-modules javafx.controls Tuito
java -cp .:bin:lib/gson-2.10.1.jar --module-path /usr/share/openjfx/lib --add-modules javafx.controls Client

java -cp .:bin:lib/gson-2.10.1.jar Server
java -cp .:bin:lib/gson-2.10.1.jar Client

windows :

java -cp ".;bin;lib/gson-2.10.1.jar" --module-path c:\\Users\\Utilisateur\\Desktop\\TéléchargementBis\\openjfx\\javafx-sdk-20.0.1\\lib\\ --add-modules javafx.controls Tuito
java -cp ".;bin;lib/gson-2.10.1.jar" --module-path c:\\Users\\Utilisateur\\Desktop\\TéléchargementBis\\openjfx\\javafx-sdk-20.0.1\\lib\\ --add-modules javafx.controls Client

java -cp ".;bin;lib/gson-2.10.1.jar" Server
java -cp ".;bin;lib/gson-2.10.1.jar" Client

#### infos

types de message (modifications possibles):

- 0 : Abonnés
- 1 : Broadcast
- 2 : Privé
- 3 : Autre (peut-être)

#### reflexion

modifier les messages (côté cient ?) pour que ceux du serveur et de soi-même ne soit pas affichés pareil
ajouter type dans le json pour broadcast, personnel, normal(abonnés)(par défaut)
faire passer l'utilisateur qui envoie les réponse de soi-même à Serveur ?
les messages supprimés restent dans la liste mais leur attribut "supprime" change
