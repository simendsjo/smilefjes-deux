(ns smilefjes.pages
  (:require [medley.core :refer [greatest-by]]
            [smilefjes.pages.search-page :as search-page]
            [smilefjes.plakaten :as plakaten]
            [smilefjes.ui :as ui]))

(defn formater-dato [dato]
  (str (.getDayOfMonth dato) "."
       (.getMonthValue dato) "."
       (.getYear dato)))

(defn siste-tilsynsresultat [siste-besøk karakter]
  [:div.bg-white.rounded.border.border-furu-500.px-5.py-2.text-center
   [:h2.text-l.flex-1 "Siste tilsynsresultat:"]
   [:img.w-36.my-2 {:title (str "Spisestedet har fått " (plakaten/beskriv-karakter karakter) ".")
                    :src (plakaten/smilefjes-svg-url karakter)}]
   (formater-dato (:tilsynsbesøk/dato siste-besøk))])

(defn spisested-info [spisested]
  (let [{:keys [linje1 linje2 poststed postnummer]} (:spisested/adresse spisested)]
    [:div
     [:h1.text-3xl (:spisested/navn spisested)]
     [:div linje1]
     [:div linje2]
     [:div postnummer " " poststed]]))

(defn mini-tilsynsresultat [besøk]
  (let [karakter (:tilsynsbesøk/smilefjeskarakter besøk)]
    [:div.py-1.text-center.flex.flex-col.items-center
     [:img.w-8.my-2 {:title (str "Spisestedet har fått " (plakaten/beskriv-karakter karakter) ".")
                     :src (plakaten/smilefjes-svg-url karakter)}]
     [:div.text-xs (formater-dato (:tilsynsbesøk/dato besøk))]]))

(defn render-spisested [ctx spisested]
  (let [besøk (->> (:tilsynsbesøk/_tilsynsobjekt spisested)
                   (sort-by :tilsynsbesøk/dato)
                   reverse)
        siste-besøk (first besøk)
        karakter (:tilsynsbesøk/smilefjeskarakter siste-besøk)]
    (ui/with-layout ctx
      (ui/header)
      [:div.bg-lysegrønn
       [:div.max-w-screen-md.mx-auto.p-5
        [:div.flex
         [:div.flex-1
          (spisested-info spisested)
          [:p.mt-5 "Tidligere tilsynsresultater:"]
          [:div.flex.gap-5
           (map mini-tilsynsresultat (take 4 besøk))]]
         (siste-tilsynsresultat siste-besøk karakter)]
        [:p.mt-5.text-xs "Mattilsynets smilefjestjeneste er nede på grunn av teknisk svikt, men vi jobber iherdig med å få på plass en erstatning. Nå har du snublet inn i vårt pågående arbeid. Kos med kaos!"]]]
      [:div
       [:div.max-w-screen-md.mx-auto.p-5
        [:h2.text-2xl "Vurdering"]
        [:p.my-2 (plakaten/oppsummer-smilefjeskarakter karakter)]]])))

(defn render-page [ctx page]
  (case (:page/kind page)
    :page.kind/spisested
    (render-spisested ctx page)

    :page.kind/spisested-index
    (search-page/render-index ctx)

    :page.kind/search-page
    (search-page/render-page ctx)

    (ui/with-layout ctx
      [:div.grid.place-items-center.h-screen
       [:div.max-w-md.p-5
        [:h1.text-xl "Smilefjes er snart tilbake"]
        [:p.mt-5 "Mattilsynets smilefjestjeneste er nede på grunn av teknisk svikt, men vi jobber iherdig med å få på plass en erstatning."]
        [:p.mt-5 "I mellomtiden kan du finne tilsynsresultater på Digitaliseringsdirektoratets tjeneste "
         [:a.text-blue-600.underline.hover:no-underline
          {:href "https://hotell.difi.no/?dataset=mattilsynet/smilefjes/tilsyn"}
          "hotell.difi.no"] "."]]])))
