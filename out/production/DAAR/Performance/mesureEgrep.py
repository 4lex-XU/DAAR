#!/usr/bin/env python3

import sys
import subprocess
import time

def main():
    if len(sys.argv) != 3:
        print(f"Usage: {sys.argv[0]} 'pattern' fichier")
        sys.exit(1)

    pattern = sys.argv[1]
    fichier = sys.argv[2]

    try:
        with open(fichier, 'r') as f:
            pass
    except FileNotFoundError:
        print(f"Erreur : Le fichier '{fichier}' n'existe pas.")
        sys.exit(1)

    start_time = time.time()

    # Exécuter egrep et afficher la sortie en temps réel
    process = subprocess.Popen(['egrep', pattern, fichier], stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)

    stdout, stderr = process.communicate()

    end_time = time.time()

    if stdout:
        print(stdout, end='')
    if stderr:
        print(stderr, end='', file=sys.stderr)

    elapsed_time = (end_time - start_time) * 1000  # Convertir en millisecondes
    print(f"Temps d'exécution : {elapsed_time:.3f} millisecondes")

if __name__ == "__main__":
    main()
