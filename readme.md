# SAE réseau/système 2024

## Juliette ROUSSELET - Jordan LAVENANT

## Création d'une appli de messagerie

### Commandes

#### linux

    sh _server_linux.sh
    sh _client_linux.sh

#### windows


    java -cp ".;bin;lib/gson-2.10.1.jar" --module-path c:\\Users\\Utilisateur\\Desktop\\TéléchargementBis\\openjfx\\javafx-sdk-20.0.1\\lib\\ --add-modules javafx.controls Tuito

    java -cp ".;bin;lib/gson-2.10.1.jar" Server

### Informations

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
    - 319 : l'id doit être un entier
  - 32 : Retour
    - 320 : Liste des utilisateurs
    - 321 : Liste des abonnés
    - 322 : Liste des abonnements
    - 323 : Liste des commandes (aide)
- 4 : Message de retour (quand un client envoie un message, le serveur lui renvoie les infos du message (notamment l'id)) (à faire)
