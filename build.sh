#!/bin/sh

# Number of clusters
# c = 5

# Number of points per clusters
# p = 50000

# Max value
# v = 1000

cd src/

mpijavac Point.java ReadCSV.java DNA_paral.java DNA_seq.java KMPoints_seq.java KMPoints_paral.java Main2D.java MainDNA.java

echo "Compile finished!"

cd ..

cd input/

python generaterawdata.py -c 5  -p 5000 -o point.csv -v 1000

python generateDnaData.py -c 5 -p 5000 -o dna.csv -l 20

cd ..

echo "Data generated in ./input! Ready to Go!"

echo "Please cd src/ and run .sh files in src folder"
