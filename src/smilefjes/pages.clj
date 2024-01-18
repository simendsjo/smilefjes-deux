(ns smilefjes.pages
  (:require [medley.core :refer [greatest-by]]))

(defn layout [& body]
  [:html
   [:body
    body]])

(defn render-spisested [spisested]
  (layout
   (let [{:keys [linje1 linje2 poststed postnummer]} (:spisested/adresse spisested)
         siste-besøk (apply greatest-by :tilsynsbesøk/dato (:tilsynsbesøk/_tilsynsobjekt spisested))]
     [:div.max-w-screen-md.p-5.mx-auto
      [:h1.text-xl (:spisested/navn spisested)]
      [:div linje1]
      [:div linje2]
      [:div postnummer " " poststed]
      [:div.bg-gray-100.p-5.mt-5.flex
       [:div
        [:h2.text-l.flex-1 "Siste tilsyn " (str (:tilsynsbesøk/dato siste-besøk))]
        [:p.text-xs.mt-1 (case (:tilsynsbesøk/smilefjeskarakter siste-besøk)
                           ("0" "1") "Tilsynet har ikke avdekket regelverksbrudd som krever oppfølging."
                           ("2" "3") "Tilsynet har avdekket regelverksbrudd som krever oppfølging.")]]
       [:img.w-14.ml-5 {:src (case (:tilsynsbesøk/smilefjeskarakter siste-besøk)
                               ("0" "1") "/images/smilefjes.svg"
                               "2" "/images/strekmunn.svg"
                               "3" "/images/surmunn.svg")}]]
      [:p.mt-5.text-xs "Mattilsynets smilefjestjeneste er nede på grunn av teknisk svikt, men vi jobber iherdig med å få på plass en erstatning. Nå har du snublet inn i vårt pågående arbeid. Kos med kaos!"]])))

(defn render-page [_ctx page]
  (case (:page/kind page)
    :page.kind/spisested
    (render-spisested page)

    (layout
     [:div.grid.place-items-center.h-screen
      [:div.max-w-md.p-5
       [:h1.text-xl "Smilefjes er snart tilbake"]
       [:p.mt-5 "Mattilsynets smilefjestjeneste er nede på grunn av teknisk svikt, men vi jobber iherdig med å få på plass en erstatning."]
       [:p.mt-5 "I mellomtiden kan du finne tilsynsresultater på Digitaliseringsdirektoratets tjeneste "
        [:a.text-blue-600.underline.hover:no-underline
         {:href "https://hotell.difi.no/?dataset=mattilsynet/smilefjes/tilsyn"}
         "hotell.difi.no"] "."]]])))
