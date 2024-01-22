(ns smilefjes.pages.search-page
  (:require [clojure.java.io :as io]
            [datomic-type-extensions.api :as d]
            [smilefjes.icons :as icons]
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

(defn svg [url]
  (slurp (io/resource (str "public" url))))

(defn render-page [ctx]
  (ui/with-layout ctx
    (ui/header)
    [:div.bg-sommerdag-200
     [:div.max-w-screen-sm.px-5.py-28.mx-auto.js-autocomplete.relative {:method :get}
      [:h1.text-3xl.mb-2 "Søk etter smilefjes"]
      [:fieldset.mt-1
       {:class [:relative :w-full "min-w-36" :h-10]}
       [:div.replicant-root {:data-view "autocomplete"}]]]]
    [:div.max-w-screen-lg.mx-auto.flex.p-8.justify-between.items-center
     [:div.max-w-72
      [:h2.text-lg.font-medium.mb-4 "Alle smilefjes på ett sted"]
      [:p.mb-4
       "Et smilefjes fra Mattilsynet viser hvordan vi vurderer viktige forhold
       som hygiene, rengjøring og vedlikehold på et spisested. Her finner du
       alle smilefjestilsyn i Norge siden 2016."]
      [:button.flex.items-center.border.rounded.px-4.py-2.font-medium.border-granskog-800.border-2.text-granskog-800
       [:div.w-4.mr-2 icons/smilefjes]
       "Les mer om smilefjes"]]
     [:div
      (svg "/images/inspektør.svg")]]
    (ui/footer)))
