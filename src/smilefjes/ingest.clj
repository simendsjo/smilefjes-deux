(ns smilefjes.ingest
  (:require [clojure.java.io :as io]
            [java-time-literals.core]
            [powerpack.logger :as log]
            [smilefjes.import.tilsyn :as tilsyn]
            [smilefjes.import.vurderinger :as vurderinger]))

(defn on-started [powerpack]
  (let [start-ms (System/currentTimeMillis)]
    (log/info "Ingesting content/tilsyn.csv ...")
    (tilsyn/transact (:datomic/conn powerpack)
                     (io/file "content/tilsyn.csv"))
    (log/info "Ingested content/tilsyn.csv in" (- (System/currentTimeMillis) start-ms) "ms."))

  (let [start-ms (System/currentTimeMillis)]
    (log/info "Ingesting content/vurderinger.csv ...")
    (vurderinger/transact (:datomic/conn powerpack)
                          (io/file "content/vurderinger.csv"))
    (log/info "Ingested content/vurderinger.csv in" (- (System/currentTimeMillis) start-ms) "ms.")))

;; Baseline:
;; [powerpack.app] Ingested all data in 02:20.709 (140709ms)

;; Med streaming, batches á 100:
;; [powerpack.app] Ran on-started hook in 00:56.799 (56799ms)

;; Med streaming og filtrering, batches á 100:
;; [powerpack.app] Ran on-started hook in 00:38.098 (38098ms)

;; Med streaming og filtrering, batches á 500:
;; [powerpack.app] Ran on-started hook in 00:37.701 (37701ms)

;; Med oppslag på entity-ref [:tilsynsbesøk/id ,,,] og [:kravpunkt/id ,,,] istedenfor upsert {}
;; [powerpack.app] Ran on-started hook in 00:34.088 (34088ms)
