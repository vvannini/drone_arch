./shell_line.sh $(($1)) $(($1 + $2 - 1)) &
./shell_line.sh $(($1 + $2)) $(($1 + $2 + $2 - 1)) &
./shell_line.sh $(($1 + $2 + $2)) $(($1 + $2 + $2 + $2 - 1)) &
./shell_line.sh $(($1 + $2 + $2 + $2)) $(($1 + $2 + $2 + $2 + $2 - 1)) &

