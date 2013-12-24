#!/bin/sh

for i in $(find . -name "*.java" -print); do
	gawk -v "infile=$i" -v "filename=$(basename $i)" -v "outfile=$i.out" -v cyear=2009 -v "author=Martin Bluemel" -f maintain-header-java.awk < "$i"
	if [ $? = 0 ]; then
		mv "$i.out" "$i"
	fi
done
