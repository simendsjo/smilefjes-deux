#!/bin/bash

echo "Checking if we should build" > preconlog.txt

./download.sh >> preconlog.txt 2>&1

if [ $? -eq 0 ]; then
    echo "Downloaded new files, build required" >> preconlog.txt
    echo "build"
else
    old_sha=$(cat data/gitsha 2> /dev/null)

    if [ "$old_sha" != "$GIT_SHA" ]; then
        echo "Git sha has changed: $old_sha > $GIT_SHA" >> preconlog.txt
        echo "Build required" >> preconlog.txt
        echo "build"
    else
        echo "No build required" >> preconlog.txt
    fi
fi

echo "Updating git sha on disk to $GIT_SHA" >> preconlog.txt
echo "$GIT_SHA" > data/gitsha
