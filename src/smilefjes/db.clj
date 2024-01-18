(ns smilefjes.db
  (:require [datomic-type-extensions.api :as d]
            [datomic-type-extensions.types :refer [define-dte]]
            [java-time-dte.install]
            [powerpack.dev :as dev]
            [powerpack.logger :as log]))

:java-time-dte.install/keep

(define-dte :data/edn :db.type/string
  [this] (pr-str this)
  [^String s] (read-string s))

(def batch-size 500)

(defn transact-batches [conn tx-seq]
  (loop [txs tx-seq]
    (let [head (take batch-size txs)
          tail (drop batch-size txs)]
      (try
        @(d/transact conn head)
        (catch Exception e
          (throw (ex-info "Failed to transact batch" {:tx head} e))))
      (when (seq tail)
        (recur tail))))
  (let [num (count tx-seq)]
    (log/info "Transacted" num "entity-maps in" (int (Math/ceil (/ num batch-size))) "batches")))

(comment
  (def conn (:datomic/conn (dev/get-app)))
  (def db (d/db conn))

  (->> (d/q '[:find ?e .
              :where
              [?e :tilsynsbesøk/id]]
            db)
       (d/entity db)
       :tilsynsbesøk/vurderinger
       (map (fn [v]
              [(:kravpunkt/id (:vurdering/kravpunkt v))
               (:vurdering/karakter v)]))
       sort)

  )
