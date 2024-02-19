# PSAR

> # Debrief du projet au 09/02

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

# Solutions possibles :
1. Utilisation de flags
2. Ajouter un id unique pour une ligne

4. Base : liste chaînée ?

6. Via socket de communication

**Mentions :**
- On gère qu'un seul fichier