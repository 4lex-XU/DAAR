# DAAR : Projet Clone de egrep

## Description
Ce projet implémente différentes méthodes d'analyse et de recherche de motifs dans des textes à l'aide d'automates et de l'algorithme KMP (Knuth-Morris-Pratt). 
Il inclut également des tests de performance avec l'outil `egrep` et génère des histogrammes pour comparer les performances de notre automate de recherche.

## Prérequis
- Java 17
- Apache Ant
- JFreeChart pour la génération de graphiques (`jfreechart-1.5.3.jar`, `jcommon-1.0.24.jar`) se trouvant dans le dossier lib
- Unix ou environnement compatible pour utiliser la commande `egrep`

Pour l'exécution avec Apache Ant :
- Etre à la racine du projet
- Pour les test automate et KMP, les paramètres sont modifiables dans le fichier build.xml, dans la balise <arg> des cibles
- Si il y a un problème de version lors de la compilation, changer la version avec celui correspondant au votre dans le fichier build.xml, dans les champs 'source' et 'target' dans la balise <javac> de la compilation 
```bash
ant clean
ant compile
ant jar
ant
```
Pour exécuter des cibles : 
- Main Automaton
```bash
ant run-automaton
```
- Main KMP
```bash
ant run-kmp
```
- Performance Automaton
```bash
ant run-performance-automaton
```
- Histogramme Automaton
```bash
ant run-histogram-automaton
```
- Performance KMP
```bash
ant run-performance-kmp
```

Pour l'exécution manuelle : 
```bash
javac -cp "lib/*" Project1-automaton/AhoUllmanMethod/*.java Project1-automaton/Performance/*.java Project1-automaton/KMP/*.java

cd Project1-automaton
java AhoUllmanMethod.Main
java KMP.Main 
java HistogramAutomaton
java Performance.PerformanceAutomaton
java Performance.KMP

rm Project1-automaton/AhoUllmanMethod/*.class Project1-automaton/Performance/*.class Project1-automaton/KMP/*.class  
```

Execution du script bash dans le dossier Performance : 
```bash
cd Project1-automaton/Performance
chmod +x measure_egrep_performance.sh
sudo apt-get install bc
./measure_egrep_performance.sh  
sudo apt install gnuplot
gnuplot histogramme.gnuplot 
```