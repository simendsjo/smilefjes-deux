(ns smilefjes.import.postnummer
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [smilefjes.db :as db]
            [smilefjes.homeless :refer [slugify]]
            [superstring.core :as str]))

(def tab-char (first "\t"))
(def csv-header [:postnummer :poststed :kommunekode :kommunenavn])

(defn normaliser-kommunenavn [s]
  (str/join " " (map str/capitalize (str/split (str/trim s) #" "))))

(defn get-kommune-link [m]
  (str "/kommune/" (slugify (:kommunenavn m)) "/"))

(defn csv-line->poststed [csv-line]
  (let [m (zipmap csv-header csv-line)
        kommunenavn (normaliser-kommunenavn
                     (:kommunenavn m))]
    {:poststed/postnummer (:postnummer m)
     :poststed/navn (:poststed m)
     :poststed/kommune {:kommune/kode (:kommunekode m)
                        :kommune/navn kommunenavn
                        :page/uri (get-kommune-link m)
                        :page/kind :page.kind/kommune
                        :page/title (str "Smilefjes for " kommunenavn)}}))

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
