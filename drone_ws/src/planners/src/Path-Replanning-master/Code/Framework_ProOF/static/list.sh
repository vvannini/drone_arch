#!/bin/bash
total=0
running=0
waiting=0
finished=0
for x in `/bin/ls ./job_local/waiting`; do
   if [ -e "$x.out" ] ; then
    var=`sed -e :a -e '$q;N;2,$D;ba' "$x.out"`
    var2="#close$results"
    if [ $var != $var2 ]; then
      echo "$x -> ($var) -> ok"
      finished=$(($finished+1))
    else
      echo "$x -> ($var) -> no"
      running=$(($running+1))
    fi
   else
    echo "$x -> ($x.out not exist) -> no"
    waiting=$(($waiting+1))
   fi
   total=$(($total+1))
done
echo "total jobs: $total | finished: $finished | running: $running | waiting: $waiting";
