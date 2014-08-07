start=$(date +'%s%3N')

java DNA_seq 4
end=$(date +'%s%3N')
diff=$(( $end - $start))
echo "DNA_SEQ Program took $diff ms"