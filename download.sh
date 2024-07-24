#!/usr/bin/env bash

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
    echo "Etag for $2 unchanged, not downloading to $1"
    return 1
  fi
}

mkdir -p data
refresh_data data/tilsyn.csv "https://data.mattilsynet.no/smilefjes-tilsyn.csv"
tilsyn=$?
refresh_data data/vurderinger.csv "https://data.mattilsynet.no/smilefjes-kravpunkter.csv"
kravpunkter=$?
