(ns smilefjes.import.tilsyn
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [clojure.set :as set]
            [datomic-type-extensions.api :as d]
            [java-time-literals.core]
            [smilefjes.db :as db]
            [superstring.core :as str])
  (:import (java.text Normalizer)
           (java.time LocalDate)))

:java-time-literals.core/keep

(defn ddmmyyyy->local-date [s]
  (let [[d1 d2 m1 m2 y1 y2 y3 y4] s]
    (LocalDate/parse
     (str y1 y2 y3 y4 "-" m1 m2 "-" d1 d2))))

(defn slugify [s]
  (-> (str/lower-case s)
      str/trim
      (Normalizer/normalize java.text.Normalizer$Form/NFD)
      (str/replace #"[\u0300-\u036F]" "")
      (str/replace #"[^a-z 0-9]" "")
      (str/replace #" +" "_")))

(defn get-tilsynsobjekt-uri
  "Hent URI-en som siden serveres fra - den inneholder kun id-en, som ikke endrer
  seg over tid."
  [m]
  (str "/smilefjes/spisested/" (str/chop-suffix (:tilsynsobjektid m) "_Tilsynsobjekt") "/"))

(defn get-tilsynsobjekt-link
  "Hent lenken som siden ble vist med i den tidligere løsningen. Denne blir
  skrevet om i nginx slik at gamle lenker fungerer mot våre nye adresser."
  [m]
  (str "/smilefjes/spisested/" (slugify (:poststed m)) "/" (slugify (:navn m)) "." (:tilsynsobjektid m) "/"))

(defn csv-line->tilsynsbesøk [csv-header csv-line]
  (let [m (zipmap csv-header csv-line)]
    {:tilsynsbesøk/id (:tilsynid m)
     :tilsynsbesøk/oppfølging? (= "1" (:tilsynsbesoektype m))
     :tilsynsbesøk/dato (ddmmyyyy->local-date (:dato m))
     :tilsynsbesøk/smilefjeskarakter (parse-long (:total_karakter m))
     :tilsynsbesøk/tilsynsobjekt {:page/uri (get-tilsynsobjekt-uri m)
                                  :page/link (get-tilsynsobjekt-link m)
                                  :page/kind :page.kind/spisested
                                  :tilsynsobjekt/id (:tilsynsobjektid m)
                                  :tilsynsobjekt/navn (str/trim (:navn m))
                                  :tilsynsobjekt/orgnummer (:orgnummer m)
                                  :tilsynsobjekt/adresse {:linje1 (:adrlinje1 m)
                                                          :linje2 (:adrlinje2 m)
                                                          :poststed (:poststed m)
                                                          :postnummer (:postnr m)}}}))

(defn transact [conn csv-file]
  (with-open [reader (io/reader csv-file)]
    (let [csv (csv/read-csv reader {:separator \;})
          csv-header (map keyword (first csv))]
      (db/transact-batches
       conn
       (for [line (next csv)]
         (csv-line->tilsynsbesøk csv-header line))))))

(comment
  (def csv
    (with-open [reader (io/reader "data/tilsyn.csv")]
      (doall
       (csv/read-csv reader {:separator \;}))))

  (def csv-header (map keyword (first csv)))
  (def csv-linjer (next csv))

  (zipmap csv-header (first csv-linjer))

  (csv-line->tilsynsbesøk csv-header (first csv-linjer))

  (first res)

  (filter #(= "Z1601041508412850239LCXIE_TilsynAvtale"
              (:tilsynsbesøk/id %))
          res)

  (def tilsyn-id-er (set (map :tilsynsbesøk/id res)))

  (count tilsyn-id-er) ;; => 44514
  (count smilefjes.import.vurderinger/tilsyn-id-er) ;; => 73704

  (count (set/intersection tilsyn-id-er
                           smilefjes.import.vurderinger/tilsyn-id-er))
  ;; => 44514

  (def res (csv->tx "data/tilsyn.csv"))
  (d/create-database "datomic:mem://lol")
  (def conn (d/connect "datomic:mem://lol"))
  @(d/transact conn (read-string (slurp (io/resource "schema.edn"))))

  (time
   (do
     @(d/transact conn res)
     nil))

  )
