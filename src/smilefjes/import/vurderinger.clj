(ns smilefjes.import.vurderinger
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [datomic-type-extensions.api :as d]
            [smilefjes.db :as db]))

(defn csv-line->vurdering [csv-header csv-line]
  (let [m (zipmap csv-header csv-line)]
    {:tilsynsbesøk/_vurderinger [:tilsynsbesøk/id (:tilsynid m)]
     :vurdering/kravpunkt [:kravpunkt/id (:ordningsverdi m)]
     :vurdering/karakter (:karakter m)}))

(defn transact [conn csv-file]
  (let [relevante-id-er (set (d/q '[:find [?id ...]
                                    :where
                                    [?e :tilsynsbesøk/id ?id]]
                                  (d/db conn)))]
    (with-open [reader (io/reader csv-file)]
      (let [csv (csv/read-csv reader {:separator \;})
            csv-header (map keyword (first csv))]
        (db/transact-batches
         conn
         (for [line (->> (next csv)
                         (filter (comp relevante-id-er first)))]
           (csv-line->vurdering csv-header line)))))))

(comment
  (def csv
    (let [s (slurp (io/file "data/vurderinger.csv"))]
      (doall
       (csv/read-csv s {:separator \;}))))

  (def csv-header (map keyword (map str/trim (first csv))))
  (def csv-linjer (next csv))
  (second csv)

  (sort (set (map (fn [[_ _ a b _ c d]] [a b c d]) csv-linjer)))

  (filter
   (fn [[_ _ a _ _ c d]] (= [a c] ["3.6" "2"]))
   csv-linjer)

  ["3.5" "2"]

  (zipmap csv-header (first csv-linjer))

  (csv-line->vurdering csv-header (first csv-linjer))

  )
