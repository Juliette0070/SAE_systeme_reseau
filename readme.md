# SAE réseau/système

## Juliette ROUSSELET

## Jordan LAVENANT

## Création d'une appli de messagerie

### commande pour compiler (linux puis windows)

javac -d bin -cp .:lib/gson-2.10.1.jar *.java

javac -d bin -cp ".;lib/gson-2.10.1.jar" *.java

### commande pour executer

java -cp .:bin:lib/gson-2.10.1.jar Server/Client

java -cp ".;bin;lib/gson-2.10.1.jar" Server/Client