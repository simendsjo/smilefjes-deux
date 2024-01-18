(ns smilefjes.pages.search-page
  (:require [datomic-type-extensions.api :as d]
            [smilefjes.link :as link]
            [smilefjes.search-index :as index]
            [smilefjes.ui :as ui]))

(defn get-spisesteder [db]
  (->> (d/q '[:find [?e ...]
              :where
              [?e :tilsynsobjekt/id]]
            db)
       (map #(d/entity db %))))

(defn format-lookup [ctx spisested]
  (let [{:keys [linje1 linje2 postnummer poststed]} (:spisested/adresse spisested)]
    [(link/link-to ctx spisested)
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
            :lookup (mapv #(format-lookup ctx %) spisesteder)}}))
