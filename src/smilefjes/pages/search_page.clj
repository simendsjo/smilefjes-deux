(ns smilefjes.pages.search-page
  (:require [datomic-type-extensions.api :as d]
            [smilefjes.search-index :as index]))

(defn get-spisesteder [db]
  (->> (d/q '[:find [?e ...]
              :where
              [?e :tilsynsobjekt/id]]
            db)
       (map #(d/entity db %))))

(defn format-lookup [spisested]
  (let [{:keys [linje1 linje2 postnummer poststed]} (:spisested/adresse spisested)]
    [(:tilsynsobjekt/id spisested)
     (:spisested/navn spisested)
     linje1 linje2
     postnummer
     poststed]))

(defn render-index [ctx]
  (let [spisesteder (get-spisesteder (:app/db ctx))]
    {:headers {"content-type" "application/json"}
     :body {:index (->> spisesteder
                        (map-indexed vector)
                        index/build-index)
            :lookup (mapv format-lookup spisesteder)}}))
