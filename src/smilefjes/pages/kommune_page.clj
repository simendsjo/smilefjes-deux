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
    [:div.bg-lav
     [:input {:type "hidden" :id "kommunekode" :value (:kommune/kode page)}]
     [:div.max-w-screen-lg.md:px-10.px-4.py-8.mx-auto.js-autocomplete.relative
      [:h1.text-3xl.mb-2 (:kommune/navn page)]
      [:p "Spisestedene er sortert etter siste tilsyn – sist besøkt øverst."]]]
    [:div.max-w-screen-lg.mx-auto.md.my-8.md:px-8
     (result/SearchResult
      {:results
       (->> (get-spisesteder page)
            (map-indexed
             (fn [idx spisested]
               (let [besøkene (tilsyn/get-besøk spisested)]
                 {:opts {:data-last_visit_date (str (:tilsynsbesøk/dato (first besøkene)))}
                  :href (link/link-to ctx spisested)
                  :title (:spisested/navn spisested)
                  :description (tilsyn/formatter-adresse (:spisested/adresse spisested))
                  :zebra? (= 1 (mod idx 2))
                  :illustrations (->> besøkene
                                      (take 4)
                                      (map (juxt :tilsynsbesøk/smilefjeskarakter
                                                 (comp str :tilsynsbesøk/dato)))
                                      (map result/prepare-illustration))}))))})]))

(defn render-list [ctx page]
  (layout/with-layout ctx page
    (layout/header)
    [:div.bg-lav
     [:div.max-w-screen-lg.md:px-10.px-4.py-8.mx-auto.js-autocomplete.relative
      [:h1.text-3xl.mb-2 "Serveringssteder i din kommune"]
      [:p "Finn din kommune for å se alle serveringsstedene vi har besøkt der du bor."]]]
    [:div.max-w-screen-lg.mx-auto.md.my-8.md:px-8
     [:ol
      (->> (d/q '[:find [?e ...]
                  :where
                  [?e :kommune/navn]]
                (:app/db ctx))
           (map #(d/entity (:app/db ctx) %))
           (sort-by :kommune/navn)
           (map-indexed
            (fn [idx kommune]
              {:href (:page/uri kommune)
               :title (:kommune/navn kommune)
               :zebra? (= 1 (mod idx 2))}))
           (map result/Result))]]))

(comment
  (def db (d/db (:datomic/conn (powerpack.dev/get-app))))
  (def page (d/entity db [:kommune/kode "3107"]))

  ;; Alle spisesteder
  (->> page :poststed/_kommune (mapcat :spisested/_poststed))

  )
