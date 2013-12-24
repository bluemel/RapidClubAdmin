#!/bin/sh

xchmod () {
	echo "processing directory \"$(pwd)\""
	for i in $(ls -a); do
		if [ -d "$i" ]; then
			if [ "$i" != "." -a "$i" != ".." ]; then
				echo "- chmod 777 \"$i\""
				chmod 777 "$i"
				cd "$i"
				xchmod
				cd ..
			fi
		else
			echo "- chmod 666 \"$i\""
			chmod 666 "$i"
		fi
	done
}

xchmod

