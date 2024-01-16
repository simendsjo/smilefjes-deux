(ns smilefjes.ingest
  (:require [java-time-literals.core]
            [powerpack.ingest :as ingest]
            [smilefjes.import.tilsyn :as tilsyn]
            [smilefjes.import.vurderinger :as vurderinger]))

(defmethod ingest/parse-file :csv [_db file-name file]
  (case file-name
    "vurderinger.csv" (vurderinger/csv->tx file)
    "tilsyn.csv" (tilsyn/csv->tx file)))

(defn on-ingested [_powerpack _results]
  ;; Inn med alle sidene
  )
