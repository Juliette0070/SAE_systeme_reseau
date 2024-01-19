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
java -cp .:bin:lib/gson-2.10.1.jar Server
java -cp .:bin:lib/gson-2.10.1.jar Client

windows :

java -cp ".;bin;lib/gson-2.10.1.jar" --module-path c:\\Users\\Utilisateur\\Desktop\\TéléchargementBis\\openjfx\\javafx-sdk-20.0.1\\lib\\ --add-modules javafx.controls Tuito
java -cp ".;bin;lib/gson-2.10.1.jar" Server
java -cp ".;bin;lib/gson-2.10.1.jar" Client

#### infos

types de message (modifications possibles):

- 0 : Abonnés
- 1 : Broadcast
- 2 : Privé
- 3 : Autre (Serveur)
  - 30 : Demande
    - 300 : Demande de pseudo
    - 301 : Demande de mot de passe
    - 302 : Demande de creation d'utilisateur (car n'existe pas)
  - 31 : Information
    - 310 : Utilisateur créé
    - 311 : Utilisateur déjà connecté
    - 312 : Mot de passe incorrect
    - 313 : Bienvenue (connexion réussie)
    - 314 : Pseudo déjà utilisé
    - 315 : Vous suivez X
    - 316 : X n'existe pas
    - 317 : Vous ne suivez plus X
    - 318 : Vous ne suivez pas X ou X n'existe pas
  - 32 : Retour
    - 320 : Liste des utilisateurs
    - 321 : Liste des abonnés
    - 322 : Liste des abonnements
    - 323 : Liste des commandes (aide)
- 4 : Message de retour (quand un client envoie un message, le serveur lui renvoie les infos du message (notamment l'id)) (à faire)

#### reflexion

modifier les messages pour que ceux du serveur et de soi-même ne soit pas affichés pareil
les messages supprimés restent dans la liste mais leur attribut "supprime" change

penser à organiser les fonctions
ajouter état de tuit'o ?

penser à changer l'utilisateur dans la zone de chat quand on change d'utilisateur ou qu'on se connecte