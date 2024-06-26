(ns smilefjes.import.tilsyn
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [clojure.set :as set]
            [datomic-type-extensions.api :as d]
            [java-time-literals.core]
            [smilefjes.adresse :as adresse]
            [smilefjes.db :as db]
            [smilefjes.homeless :refer [slugify]]
            [smilefjes.tilsyn :as tilsyn]
            [superstring.core :as str])
  (:import (java.time LocalDate)))

:java-time-literals.core/keep

(defn ddmmyyyy->local-date [s]
  (let [[d1 d2 m1 m2 y1 y2 y3 y4] s]
    (LocalDate/parse
     (str y1 y2 y3 y4 "-" m1 m2 "-" d1 d2))))

(defn get-id [m]
  (str/chop-suffix (:tilsynsobjektid m) "_Tilsynsobjekt"))

(defn get-tilsynsobjekt-uri
  "Hent URI-en som siden serveres fra - den inneholder kun id-en, som ikke endrer
  seg over tid."
  [m]
  (str "/spisested/" (get-id m) "/"))

(defn get-tilsynsobjekt-link
  "Lenken var opprinnelig den samme som den gamle løsningen brukte. Ettersom den
  inneholder en del overflødig informasjon har vi lagt en redirect i nginx for å
  kvitte oss med litt ræl, men valgte allikevel å beholde litt meningsbærende
  informasjon fordi det er hyggelig. Uansett blir denne skrevet om i nginx til å
  treffe på URI-en til siden."
  [m]
  (str "/spisested/" (or (not-empty (slugify (:poststed m))) "_") "/"
       (slugify (:navn m)) "." (get-id m) "/"))

(def ikke-inkludert-besøk?
  #{"Z2406251324396200239ZDBTB_TilsynAvtale"})

(defn csv-line->tilsynsbesøk [csv-header csv-line ikke-omfattet-id?]
  (let [m (zipmap csv-header csv-line)
        adresse {:linje1 (adresse/normalize-adresse (:adrlinje1 m))
                 :linje2 (adresse/normalize-adresse (:adrlinje2 m))
                 :poststed (adresse/normalize-poststed (:poststed m))
                 :postnummer (:postnr m)}
        navn (str/trim (:navn m))]
    (when (and (not (ikke-omfattet-id? (:tilsynsobjektid m)))
               (not (ikke-inkludert-besøk? (:tilsynid m))))
      {:tilsynsbesøk/id (:tilsynid m)
       :tilsynsbesøk/oppfølging? (= "1" (:tilsynsbesoektype m))
       :tilsynsbesøk/dato (ddmmyyyy->local-date (:dato m))
       :tilsynsbesøk/smilefjeskarakter (:total_karakter m)
       :tilsynsbesøk/tilsynsobjekt
       {:page/uri (get-tilsynsobjekt-uri m)
        :page/link (get-tilsynsobjekt-link m)
        :page/kind :page.kind/spisested
        :page/title (str "Smilefjes: " navn)
        :open-graph/description (str "Smilefjes: Tilsynsresultat for " navn
                                     (when-let [adresse (not-empty (tilsyn/formatter-adresse adresse))]
                                       (str ", " adresse)))
        :tilsynsobjekt/id (get-id m)
        :spisested/navn navn
        :spisested/orgnummer (:orgnummer m)
        :spisested/adresse adresse
        :spisested/poststed {:poststed/postnummer (:postnr m)}}
       :tilsynsbesøk/vurderinger [{:vurdering/kravpunkt [:kravpunkt/id "1"]
                                   :vurdering/karakter (:karakter1 m)}
                                  {:vurdering/kravpunkt [:kravpunkt/id "2"]
                                   :vurdering/karakter (:karakter2 m)}
                                  {:vurdering/kravpunkt [:kravpunkt/id "3"]
                                   :vurdering/karakter (:karakter3 m)}
                                  {:vurdering/kravpunkt [:kravpunkt/id "4"]
                                   :vurdering/karakter (:karakter4 m)}]})))

(defn transact [conn csv-file ikke-omfattet-id?]
  (with-open [reader (io/reader csv-file)]
    (let [csv (csv/read-csv reader {:separator \;})
          csv-header (map keyword (first csv))]
      (db/transact-batches
       conn
       (keep #(csv-line->tilsynsbesøk csv-header % ikke-omfattet-id?)
             (next csv))))))

(comment
  (def csv
    (with-open [reader (io/reader "data/tilsyn.csv")]
      (doall
       (csv/read-csv reader {:separator \;}))))

  (d/create-database "datomic:mem://lol")
  (def conn (d/connect "datomic:mem://lol"))
  @(d/transact conn (read-string (slurp (io/resource "schema.edn"))))
  @(d/transact conn (read-string (slurp (io/file "content/kravpunkter.edn"))))

  (doseq [linje csv-linjer]
    (try
      @(d/transact conn [(csv-line->tilsynsbesøk csv-header linje)])
      (catch Exception e
        (throw (ex-info "Niks" {:linje linje} e)))))

  (def csv-header (map keyword (first csv)))
  (def csv-linjer (next csv))

  (zipmap csv-header (first csv-linjer))

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
