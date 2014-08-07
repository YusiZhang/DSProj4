start=$(date +'%s%3N')

mpirun -np 12 java MainDNA 4
end=$(date +'%s%3N')
diff=$(( $end - $start))
echo "DNA_paral Program took $diff ms"