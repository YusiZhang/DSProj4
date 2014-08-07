start=$(date +'%s%3N')

java KMPoints_seq 4
end=$(date +'%s%3N')
diff=$(( $end - $start))
echo "KMPoints_seq Program took $diff ms"