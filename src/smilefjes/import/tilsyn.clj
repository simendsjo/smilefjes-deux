(ns smilefjes.import.tilsyn
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [datomic-type-extensions.api :as d]
            [java-time-literals.core])
  (:import (java.time LocalDate)))

:java-time-literals.core/keep

(defn ddmmyyyy->local-date [s]
  (let [[d1 d2 m1 m2 y1 y2 y3 y4] s]
    (LocalDate/parse
     (str y1 y2 y3 y4 "-" m1 m2 "-" d1 d2))))

(defn csv-line->tilsynsbesøk [csv-header csv-line]
  (let [m (zipmap csv-header csv-line)]
    {:tilsynsbesøk/id (:tilsynid m)
     :tilsynsbesøk/oppfølging? (= "1" (:tilsynsbesoektype m))
     :tilsynsbesøk/dato (ddmmyyyy->local-date (:dato m))
     :tilsynsbesøk/smilefjeskarakter (parse-long (:total_karakter m))
     :tilsynsbesøk/tilsynsobjekt {:tilsynsobjekt/id (:﻿tilsynsobjektid m)
                                  :tilsynsobjekt/navn (:navn m)
                                  :tilsynsobjekt/orgnummer (:orgnummer m)
                                  :tilsynsobjekt/adresse {:linje1 (:adrlinje1 m)
                                                          :linje2 (:adrlinje2 m)
                                                          :poststed (:poststed m)
                                                          :postnummer (:postnr m)}}}))

(defn parse [csv-file]
  (let [csv (with-open [reader (io/reader csv-file)]
              (doall
               (csv/read-csv reader {:separator \;})))
        csv-header (map keyword (first csv))]
    (for [line (next csv)]
      (csv-line->tilsynsbesøk csv-header line))))

(comment
  (def csv
    (with-open [reader (io/reader "content/tilsyn.csv")]
      (doall
       (csv/read-csv reader {:separator \;}))))

  (def csv-header (map keyword (first csv)))
  (def csv-linjer (next csv))

  (zipmap csv-header (first csv-linjer))

  (csv-line->tilsynsbesøk csv-header (first csv-linjer))

  (def res (parse "content/tilsyn.csv"))
  (d/create-database "datomic:mem://lol")
  (def conn (d/connect "datomic:mem://lol"))
  @(d/transact conn (read-string (slurp (io/resource "schema.edn"))))

  (time
   (do
     @(d/transact conn res)
     nil))

  )
