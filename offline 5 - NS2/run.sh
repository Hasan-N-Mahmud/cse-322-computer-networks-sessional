#!/bin/sh
echo "Shell script running"
for i in {0..14}
do
 ns wireless.tcl $i
done
#python3 Graph.py
