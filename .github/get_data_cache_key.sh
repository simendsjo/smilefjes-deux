#!/bin/bash

get_etag() {
    curl -sI "$1" | grep -i etag | awk '{print $2}' | tr -d '"' | tr -d '[:space:]'
}

tilsyn_etag=$(get_etag "https://hotell.difi.no/download/mattilsynet/smilefjes/tilsyn?download")
kravpunkter_etag=$(get_etag "https://hotell.difi.no/download/mattilsynet/smilefjes/kravpunkter?download")

echo "tilsyn_$tilsyn_etag/kravpunkter_$kravpunkter_etag"
