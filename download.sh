#!/bin/bash

refresh_data() {
  local current_etag
  current_etag=$(cat "$1.etag" 2> /dev/null)
  local etag
  etag=$(curl -sI "$2" | grep -i etag | awk '{print $2}' | tr -d '"')

  if [ "$current_etag" != "$etag" ]; then
    echo "Downloading $2 to $1"
    curl -s "$2" | sed '1s/^\xEF\xBB\xBF//' > "$1"
    echo "$etag" > "$1.etag"
    return 0
  else
    return 1
  fi
}

mkdir -p data
refresh_data data/tilsyn.csv "https://hotell.difi.no/download/mattilsynet/smilefjes/tilsyn?download"
tilsyn=$?
refresh_data data/vurderinger.csv "https://hotell.difi.no/download/mattilsynet/smilefjes/kravpunkter?download"
kravpunkter=$?
