#!/bin/bash

./download.sh

if [ $? -eq 0 ]; then
    echo "build"
else
    old_sha=$(cat data/gitsha 2> /dev/null)

    if [ "$old_sha" != "$GIT_SHA" ]; then
        echo "build"
    fi
fi

echo "$GIT_SHA" > data/gitsha
