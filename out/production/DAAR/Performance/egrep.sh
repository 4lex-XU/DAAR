#!/bin/bash

# Vérifier que les arguments sont fournis
if [ "$#" -ne 2 ]; then
    echo "Usage: $0 'pattern' fichier"
    exit 1
fi

PATTERN=$1
FICHIER=$2

# Vérifier que le fichier existe
if [ ! -f "$FICHIER" ]; then
    echo "Erreur : Le fichier '$FICHIER' n'existe pas."
    exit 1
fi

# Enregistrer le temps de début avec précision en nanosecondes
START_TIME=$(date +%s.%3N)

# Exécuter la commande egrep
egrep "$PATTERN" "$FICHIER"

# Enregistrer le temps de fin
END_TIME=$(date +%s.%3N)

# Calculer la différence en millisecondes
ELAPSED_TIME=$(echo "scale=3; ($END_TIME - $START_TIME) * 1000" | bc)

echo "Temps d'exécution : ${START_TIME} millisecondes"
echo "Temps d'exécution : ${END_TIME} millisecondes"

echo "Temps d'exécution : ${ELAPSED_TIME} millisecondes"
