#!/bin/bash 
start=`date +%s`
ls -al >/dev/null 2>&1
sleep 10s
java SIMMedian parameter1
end=`date +%s` 
dif=$[end - start]
echo 'time='$dif
#echo $[$(date +%s.%N)/1000000]


