#!/bin/bash

set -x 

read -p "Entrer le nom du microservice: " microservice
patternProjet='template'
patternProjetCap='Template'
cp -r rudi-microservice-template "rudi-microservice-${microservice}"
rm -rf "rudi-microservice-${microservice}/rudi-microservice-template-core/target"
rm -rf "rudi-microservice-${microservice}/rudi-microservice-template-storage/target"
rm -rf "rudi-microservice-${microservice}/rudi-microservice-template-service/target"
rm -rf "rudi-microservice-${microservice}/rudi-microservice-template-facade/target"
mv "rudi-microservice-${microservice}/rudi-microservice-template-core/src/main/java/org/rudi/microservice/template" "rudi-microservice-${microservice}/rudi-microservice-template-core/src/main/java/org/rudi/microservice/${microservice}"
mv "rudi-microservice-${microservice}/rudi-microservice-template-core" "rudi-microservice-${microservice}/rudi-microservice-${microservice}-core" 
mv "rudi-microservice-${microservice}/rudi-microservice-template-storage/src/main/java/org/rudi/microservice/template" "rudi-microservice-${microservice}/rudi-microservice-template-storage/src/main/java/org/rudi/microservice/${microservice}"
mv "rudi-microservice-${microservice}/rudi-microservice-template-storage" "rudi-microservice-${microservice}/rudi-microservice-${microservice}-storage" 
mv "rudi-microservice-${microservice}/rudi-microservice-template-service/src/main/java/org/rudi/microservice/template" "rudi-microservice-${microservice}/rudi-microservice-template-service/src/main/java/org/rudi/microservice/${microservice}"
mv "rudi-microservice-${microservice}/rudi-microservice-template-service" "rudi-microservice-${microservice}/rudi-microservice-${microservice}-service"
mv "rudi-microservice-${microservice}/rudi-microservice-template-facade/src/main/java/org/rudi/microservice/template" "rudi-microservice-${microservice}/rudi-microservice-template-facade/src/main/java/org/rudi/microservice/${microservice}"
mv "rudi-microservice-${microservice}/rudi-microservice-template-facade" "rudi-microservice-${microservice}/rudi-microservice-${microservice}-facade"

cd "rudi-microservice-${microservice}"

sed -i "s/templateDirectory/aaaaDirectory/g" "rudi-microservice-${microservice}-core/pom.xml"
sed -i "s/templateDirectory/aaaaDirectory/g" "rudi-microservice-${microservice}-facade/pom.xml"
sed "s/restTemplate/aaaaTemplate/g" "rudi-microservice-${microservice}-facade/src/main/java/org/rudi/microservice/${microservice}/facade/config/security/WebSecurityConfig.java"
sed "s/RestTemplate/AaaaTemplate/g" "rudi-microservice-${microservice}-facade/src/main/java/org/rudi/microservice/${microservice}/facade/config/security/WebSecurityConfig.java"

find . -name "*.*" -print0 | while read -d $'\0' file
do
	if [[ -f $file ]]; then #teste si c'est un fichier
		filename="$(basename -- $file)" #donne le nom du fichier et son extension sans le chemin
		if [[ $filename != "rename.sh" ]]; then
			sed -i "s/$patternProjet/$microservice/g" $file #remplace toutes les occurences de $patternProjet dans le fichier par $projet
			sed -i "s/$patternProjetCap/${microservice^}/g" $file #remplace toutes les occurences de $patternProjetCap dans le fichier par $projet
			
			echo "$filename a été parcouru"
			if [[ $filename == *"$patternProjet"* ]]; then #teste si $filename contient $patternProjet
				newfilename="${filename//$patternProjet/$microservice}" #remplace $patternProjet dans le nom du fichier par $projet
				newfile="${file//$filename/$newfilename}" #donne le chemin complet avec le nouveau nom de fichier
				mv $file $newfile #renommage du fichier
				echo "$filename renommé en $newfilename"
			fi
			if [[ $filename == *"$patternProjetCap"* ]]; then #teste si $filename contient $patternProjetCap
				newfilename="${filename//$patternProjetCap/${microservice^}}" #remplace $patternProjet dans le nom du fichier par $projet
				newfile="${file//$filename/$newfilename}" #donne le chemin complet avec le nouveau nom de fichier
				mv $file $newfile #renommage du fichier
				echo "$filename renommé en $newfilename"
			fi
		fi
	fi
done

sed -i "s/aaaaDirectory/templateDirectory/g" "rudi-microservice-${microservice}-core/pom.xml"
sed -i "s/aaaaDirectory/templateDirectory/g" "rudi-microservice-${microservice}-facade/pom.xml"
sed "s/aaaaTemplate/restTemplate/g" "rudi-microservice-${microservice}-facade/src/main/java/org/rudi/microservice/${microservice}/facade/config/security/WebSecurityConfig.java"
sed "s/AaaaTemplate/RestTemplate/g" "rudi-microservice-${microservice}-facade/src/main/java/org/rudi/microservice/${microservice}/facade/config/security/WebSecurityConfig.java"


echo "L'exécution du script est terminé..."
echo "Vous pouvez fermer la fenêtre"
sleep 60
