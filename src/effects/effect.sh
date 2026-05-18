text="Reconnecting to server..."
tput civis

# gray white gradient
colors=(234 236 238 240 242 244 246 248 250 252 254 255 254 252 250 248 246 244 242 240 238 236)
col_len=${#colors[@]}

while true; do
    echo -ne "\r"
    ((step++))

    for (( i=0; i<${#text}; i++ )); do
        # Change (i + step) to (step - i) to set left to right
        # col_len for result is not < 0 when mod
        idx=$(( (step - i + col_len * 10) % col_len ))
        
        color_code=${colors[$idx]}
        echo -ne "\e[38;5;${color_code}m${text:$i:1}\e[0m"
    done
    
    sleep 0.05 # speed
done
