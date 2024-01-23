(ns smilefjes.pages.kommune-page
  (:require [datomic-type-extensions.api :as d]
            [smilefjes.components.search-result :as result]
            [smilefjes.layout :as layout]
            [smilefjes.link :as link]
            [smilefjes.tilsyn :as tilsyn]))

(defn siste-tilsyn [spisested]
  (->> (:tilsynsbesøk/_tilsynsobjekt spisested)
       (map :tilsynsbesøk/dato)
       sort
       last))

(defn get-spisesteder [page]
  (->> (:poststed/_kommune page)
       (mapcat :spisested/_poststed)
       (sort-by siste-tilsyn)
       reverse))

(defn render [ctx page]
  (layout/with-layout ctx page
    (layout/header)
    [:div.bg-lysegrønn
     [:div.max-w-screen-md.px-5.py-8.mx-auto.js-autocomplete.relative
      [:h1.text-3xl.mb-2 (:kommune/navn page)]
      [:p "Spisestedene er sortert etter siste tilsyn – de som sist ble besøkt øverst."]]]
    [:div.max-w-screen-lg.mx-auto.md.my-8.px-8
     (result/SearchResult
      {:results
       (->> (get-spisesteder page)
            (map-indexed
             (fn [idx spisested]
               {:href (link/link-to ctx spisested)
                :title (:spisested/navn spisested)
                :description (tilsyn/formatter-adresse (:spisested/adresse spisested))
                :zebra? (= 1 (mod idx 2))
                :illustrations (->> (tilsyn/get-besøk spisested)
                                    (take 4)
                                    (map (juxt :tilsynsbesøk/smilefjeskarakter
                                               (comp str :tilsynsbesøk/dato)))
                                    (map result/prepare-illustration))})))})]))

(comment
  (def db (d/db (:datomic/conn (powerpack.dev/get-app))))
  (def page (d/entity db [:kommune/kode "3107"]))

  ;; Alle spisesteder
  (->> page :poststed/_kommune (mapcat :spisested/_poststed))

  )
