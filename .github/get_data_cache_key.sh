#!/bin/bash

get_etag() {
    curl -sI "$1" | grep -i etag | awk '{print $2}' | tr -d '"' | tr -d '[:space:]'
}

tilsyn_etag=$(get_etag "https://data.mattilsynet.no/smilefjes-tilsyn.csv")
kravpunkter_etag=$(get_etag "https://data.mattilsynet.no/smilefjes-kravpunkter.csv")

echo "tilsyn_$tilsyn_etag/kravpunkter_$kravpunkter_etag"
