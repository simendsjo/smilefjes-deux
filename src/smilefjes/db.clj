(ns smilefjes.db
  (:require [clojure.java.io :as io]
            [datomic-type-extensions.api :as d]
            [datomic-type-extensions.types :refer [define-dte]]
            [java-time-dte.install]
            [powerpack.logger :as log]))

:java-time-dte.install/keep

(define-dte :data/edn :db.type/string
  [this] (pr-str this)
  [^String s] (read-string s))

(defn create-database [uri schema]
  (d/create-database uri)
  (let [conn (d/connect uri)]
    @(d/transact conn schema)
    conn))

(def batch-size 500)

(defn transact-batches [conn tx-seq]
  (loop [txs tx-seq]
    (let [head (take batch-size txs)
          tail (drop batch-size txs)]
      @(d/transact conn head)
      (when (seq tail)
        (recur tail))))
  (let [num (count tx-seq)]
    (log/info "Transacted" num "entity-maps in" (int (Math/ceil (/ num batch-size))) "batches")))

(comment
  (def conn (create-database
             "datomic:mem://smilefjes"
             (read-string (slurp (io/resource "schema.edn")))))


  )
