(ns smilefjes.import.postnummer
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [smilefjes.db :as db]
            [superstring.core :as str]))

(def tab-char (first "\t"))
(def csv-header [:postnummer :poststed :kommunekode :kommunenavn])

(defn normaliser-kommunenavn [s]
  (str/join " " (map str/capitalize (str/split (str/trim s) #" "))))

(defn csv-line->poststed [csv-line]
  (let [m (zipmap csv-header csv-line)]
    {:poststed/postnummer (:postnummer m)
     :poststed/navn (:poststed m)
     :poststed/kommune {:kommune/kode (:kommunekode m)
                        :kommune/navn (normaliser-kommunenavn
                                       (:kommunenavn m))}}))

(defn transact [conn csv-file]
  (with-open [reader (io/reader csv-file)]
    (let [csv (csv/read-csv reader {:separator tab-char})]
      (db/transact-batches
       conn
       (map csv-line->poststed csv)))))

(comment
  (def csv
    (with-open [reader (io/reader "data/postnummer.csv")]
      (doall
       (csv/read-csv reader {:separator tab-char}))))


  (def csv-linjer csv)

  (zipmap csv-header (first csv-linjer))


  )
