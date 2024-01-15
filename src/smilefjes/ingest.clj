(ns smilefjes.ingest
  (:require [java-time-literals.core]
            [powerpack.ingest :as ingest]
            [smilefjes.import.tilsyn :as tilsyn]))

(defmethod ingest/parse-file :csv [_db file-name file]
  (case file-name
    "kravpunkter.csv" []
    "tilsyn.csv" (tilsyn/parse file)))

(defn on-ingested [powerpack results]
  ;; Inn med alle sidene
  )
