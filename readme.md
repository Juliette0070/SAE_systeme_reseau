# SAE réseau/système

## Juliette ROUSSELET

## Jordan LAVENANT

## Création d'une appli de messagerie

### commande pour compiler (linux puis windows)

javac -d bin -cp .:lib/gson-2.10.1.jar *.java

javac -d bin -cp ".;lib/gson-2.10.1.jar" *.java

### commande pour executer

java -cp .:bin:lib/gson-2.10.1.jar Server
java -cp .:bin:lib/gson-2.10.1.jar Client

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
