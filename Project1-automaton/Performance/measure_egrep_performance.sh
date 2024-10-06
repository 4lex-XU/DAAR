#!/bin/bash

# Répertoire de test (non utilisé dans le script actuel, à supprimer ou à utiliser si nécessaire)
TEST_DIR="test_files"

# Définir PATTERNS comme un tableau pour gérer correctement les motifs avec des caractères spéciaux
PATTERNS=(
    "a"
    "a|b|c|d"
    "a.b"
    "ab*"
    "(a|b)*"
    "a|bc*"
    "Sargon"
)

# Liste des fichiers à analyser
FILES=(
    "../Texts/Babylon.txt"
    "../Texts/D_Europe_en_Amérique_par_le_pôle_nord.txt"
    "../Texts/De_Duodecim_Abusionibus_Saeculi.txt"
    "../Texts/Franc-Maçonnerie.txt"
    "../Texts/PonyTracks.txt"
    "../Texts/Sonniyhdistystä.txt"
    "../Texts/The_literature_of_the_Highlanders.txt"
    "../Texts/What_to_draw_and_how_to_draw_it.txt"
)

RESULT_FILE="egrep_performance_results.txt"
TEMP_FILE="egrep_temp_results.txt"

# Supprimer le fichier de résultats existant s'il existe
if [ -f "$RESULT_FILE" ]; then
    rm "$RESULT_FILE"
fi

# Ajouter un en-tête au fichier de résultats
echo "Pattern, Temps_total (secondes)" > "$RESULT_FILE"

# Boucle sur chaque motif
for PATTERN in "${PATTERNS[@]}"; do
    total_time=0.0

    # Boucle sur chaque fichier
    for FILE_PATH in "${FILES[@]}"; do
        if [ -f "$FILE_PATH" ]; then
            # Mesurer le temps d'exécution de egrep
            start_time=$(date +%s.%3N)
            egrep "$PATTERN" "$FILE_PATH" > "$TEMP_FILE"
            end_time=$(date +%s.%3N)

            # Calculer le temps écoulé
            elapsed_time=$(echo "$end_time - $start_time" | bc)
            total_time=$(echo "$total_time + $elapsed_time" | bc)
        else
            echo "Fichier non trouvé : $FILE_PATH"
        fi
    done

    # Ajouter le résultat au fichier avec le motif entre guillemets pour gérer les virgules ou espaces
    echo "\"$PATTERN\", $total_time" >> "$RESULT_FILE"
done

# Supprimer le fichier temporaire
rm -f "$TEMP_FILE"

echo "Résultats enregistrés dans $RESULT_FILE"
