#!/bin/bash

# This script automates the emailing of assignment and labtests to students.
# Change the subject on line 12 as appropriate.
# Copyright (C) 2012 by Steven J. Castellucci

echo ""
echo "Mailing results to..."
for name in `ls -1 *.txt | cut -d "." -f 1`
do
	echo "$name@cse.yorku.ca"
	mail -s "EECS1020 Test1 Multiple Choice Results" -r "steven_c@yorku.ca" $name@cse.yorku.ca < $name.txt
done
echo "...done."
echo ""
