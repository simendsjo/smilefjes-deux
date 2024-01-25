(ns smilefjes.pages.search-page
  (:require [clojure.java.io :as io]
            [datomic-type-extensions.api :as d]
            [fontawesome.icons :as icons]
            [smilefjes.layout :as layout]
            [smilefjes.link :as link]
            [smilefjes.search-index :as index]
            [smilefjes.tilsyn :as tilsyn]))

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
     poststed
     (for [besøk (take 4 (tilsyn/get-besøk spisested))]
       [(:tilsynsbesøk/smilefjeskarakter besøk)
        (str (:tilsynsbesøk/dato besøk))])]))

(defn render-index [ctx]
  (let [spisesteder (get-spisesteder (:app/db ctx))]
    {:headers {"content-type" "application/json"}
     :body {:index (->> spisesteder
                        (map-indexed vector)
                        index/build-index)
            :lookup (mapv #(format-lookup ctx %) spisesteder)}}))

(defn svg [url]
  (slurp (io/resource (str "public" url))))

(defn render-page [ctx page]
  (layout/with-layout ctx page
    (layout/header)
    [:div.bg-sommerdag-200
     [:div.max-w-screen-sm.px-5.py-8.md:py-28.mx-auto.js-autocomplete.relative
      [:h1.text-3xl.mb-2 "Søk etter smilefjes"]
      [:fieldset.mt-1
       {:class [:relative :w-full "min-w-36" :h-10]}
       [:div.replicant-root {:data-view "autocomplete"}]]]]
    [:div.max-w-screen-lg.mx-auto.sm:flex.px-8.py-4.md:py-8.justify-around.items-center
     [:div.max-w-72.py-4.md
      [:h2.text-lg.font-medium.mb-4 "Alle smilefjes på ett sted"]
      [:p.mb-4
       "Et smilefjes fra Mattilsynet viser hvordan vi vurderer viktige forhold
       som hygiene, rengjøring og vedlikehold på et spisested. Her finner du
       alle smilefjestilsyn i Norge siden 2016."]
      [:a.inline-flex.items-center.border.rounded.px-4.py-2.font-medium.border-granskog-800.border-2.text-granskog-800.hover:bg-lav.transition
       {:href "https://www.mattilsynet.no/mat-og-drikke/forbrukere/smilefjesordningen"}
       "Les mer om smilefjes"
       [:div.w-4.ml-2 (icons/render :fontawesome.solid/arrow-right)]]]
     [:div.max-w-80.py-4.w-full
      (svg "/images/inspektør.svg")]]))

(defn render-result-page [ctx page]
  (layout/with-layout ctx page
    (layout/header)
    [:div.bg-sommerdag-200
     [:div.max-w-screen-sm.px-5.py-8.mx-auto.js-autocomplete.relative
      [:h1.text-3xl.mb-2 "Søk etter smilefjes"]
      [:fieldset.mt-1
       {:class [:relative :w-full "min-w-36" :h-10]}
       [:div.replicant-root {:data-view "search-form"}]]]]
    [:div.max-w-screen-lg.mx-auto.md.my-8.md:px-8
     [:div.replicant-root {:data-view "search-result"}]]))
