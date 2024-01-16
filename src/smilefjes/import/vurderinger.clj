(ns smilefjes.import.vurderinger
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [clojure.string :as str]))

(defn csv-vurdering->vurdering [csv-vurdering]
  {:vurdering/tilsynsbesøk {:tilsynsbesøk/id (:tilsynid csv-vurdering)}
   :vurdering/kravpunkt {:kravpunkt/id (:ordningsverdi csv-vurdering)}
   :vurdering/karakter (:karakter csv-vurdering)})

(defn parse [csv-file]
  (let [csv (with-open [reader (io/reader csv-file)]
              (doall
               (csv/read-csv reader {:separator \;})))
        csv-header (map keyword (first csv))]
    (for [csv-line (next csv)]
      (zipmap csv-header csv-line))))

(defn csv-vurderinger->kravpunkter [res]
  (for [[id karakterer] (group-by first (set
                                         (map (juxt :ordningsverdi :karakter :tekst_no)
                                              res)))]
    {:kravpunkt/id id
     :kravpunkt/hovedområde (case (first id)
                              \1 "1. Rutiner og ledelse"
                              \2 "2. Lokaler og utstyr"
                              \3 "3. Mathåndtering og tilberedning"
                              \4 "4. Sporbarhet og merking")
     :kravpunkt/karakter->tekst (into {}
                                      (for [[_ karakter tekst] karakterer]
                                        [karakter tekst]))}))

(defn csv->tx [csv-file]
  (let [csv-vurderinger (parse csv-file)]
    (concat
     (csv-vurderinger->kravpunkter csv-vurderinger)
     (map csv-vurdering->vurdering csv-vurderinger))))

(comment
  (def csv
    (let [s (slurp (io/file "content/vurderinger.csv"))]
      (doall
       (csv/read-csv s {:separator \;}))))

  (def csv-header (map keyword (map str/trim (first csv))))
  (def csv-linjer (next csv))

  (zipmap csv-header (first csv-linjer))

  (def csv-vurderinger (parse "content/vurderinger.csv"))

  (def tilsyn-id-er
    (set (map :tilsynid csv-vurderinger)))

  (first csv-vurderinger)

  (first csv-vurderinger)

  (count csv-linjer)

  (take 50 (csv->tx "content/vurderinger.csv"))

  (csv-vurderinger->kravpunkter csv-vurderinger)
  )
