#!/bin/bash
for i in {0..2049}
#Parei no 620. continuar do 2049 - 620
do
	echo "Mission $i"
   ./init.bash $i
done

for i in {18425..22523}
do
	echo "Mission $i"
   ./init.bash $i
done

for i in {38899..40949}
do
	echo "Mission $i"
   ./init.bash $i
done