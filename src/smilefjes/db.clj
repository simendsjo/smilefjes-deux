(ns smilefjes.db
  (:require [clojure.java.io :as io]
            [datomic-type-extensions.api :as d]
            [datomic-type-extensions.types :refer [define-dte]]
            [java-time-dte.install]))

:java-time-dte.install/keep

(define-dte :data/edn :db.type/string
  [this] (pr-str this)
  [^String s] (read-string s))

(define-dte :i18n/edn :db.type/string
  [this] (pr-str this)
  [^String s] (read-string s))

(defn create-database [uri schema]
  (d/create-database uri)
  (let [conn (d/connect uri)]
    @(d/transact conn schema)
    conn))

(comment
  (def conn (create-database
             "datomic:mem://smilefjes"
             (read-string (slurp (io/resource "schema.edn")))))


  )
