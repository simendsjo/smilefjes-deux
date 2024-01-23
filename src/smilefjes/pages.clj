(ns smilefjes.pages
  (:require [smilefjes.pages.search-page :as search-page]
            [smilefjes.pages.spisested-page :as spisested-page]
            [smilefjes.ui :as ui]))

(defn render-page [ctx page]
  (case (:page/kind page)
    :page.kind/frontpage
    (search-page/render-page ctx page)

    :page.kind/spisested
    (spisested-page/render ctx page)

    :page.kind/spisested-index
    (search-page/render-index ctx)

    :page.kind/search-page
    (search-page/render-result-page ctx page)

    (ui/with-layout ctx page
      [:div.grid.place-items-center.h-screen
       [:div.max-w-md.p-5
        [:h1.text-xl "Smilefjes er snart tilbake"]
        [:p.mt-5 "Mattilsynets smilefjestjeneste er nede på grunn av teknisk svikt, men vi jobber iherdig med å få på plass en erstatning."]
        [:p.mt-5 "I mellomtiden kan du finne tilsynsresultater på Digitaliseringsdirektoratets tjeneste "
         [:a.text-blue-600.underline.hover:no-underline
          {:href "https://hotell.difi.no/?dataset=mattilsynet/smilefjes/tilsyn"}
          "hotell.difi.no"] "."]]])))
