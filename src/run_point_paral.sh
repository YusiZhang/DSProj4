start=$(date +'%s%3N')

mpirun -np 12 java Main2D 4
end=$(date +'%s%3N')
diff=$(( $end - $start))
echo "KMPoints_paral Program took $diff ms"
