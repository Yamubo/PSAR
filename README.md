# PSAR

> # Debrief du projet 

# Fonctionnalités voulues :
- Ajouter + insérer une ligne
- Modifier/éditer une ligne
- Supprimer une ligne
- Affichage complet du texte (toutes les lignes ou celles sélectionnées)

# Problèmes notés:
1. Blocage de la ligne pour les autres utilisateurs lors de l'édition par un utilisateur
2. Nécessité de gérer l'ordre d'insertion
3. Propagation des modifications aux autres utilisateurs
4. Stocker les lignes dans une Structure de données adaptée
5. Choix d'un protocole pertinent
6. Communications entre "serveurs" et "users"

# Solutions :
1. Utilisation de flags et lock
2. Ajouter un id unique pour une ligne
3. Envoyer l'update à chaque client connecté pour toutes les modifications du fichier 
4. Liste chaînée de Ligne: (identifiant + chaine de caractère)
5. utilisation du protocol update
6. Via socket de communication

# Lancement en local
1. compilation
    ```
        make
    ```
2. Client: 
    ```
        java Client.Client
    ```
3. Serveur:
    ```
        java Serveur.Serveur
    ```