(ns smilefjes.pages)

(defn layout [& body]
  [:html
   [:body
    body]])

(defn oops [tittel]
  [:div.max-w-md.p-5
   [:h1.text-xl tittel]
   [:p.mt-5 "Mattilsynets smilefjestjeneste er nede på grunn av teknisk svikt, men vi jobber iherdig med å få på plass en erstatning."]
   [:p.mt-5 "I mellomtiden kan du finne tilsynsresultater på Digitaliseringsdirektoratets tjeneste "
    [:a.text-blue-600.underline.hover:no-underline
     {:href "https://hotell.difi.no/?dataset=mattilsynet/smilefjes/tilsyn"}
     "hotell.difi.no"] "."]])

(defn render-spisested [spisested]
  (layout
   [:div.grid.place-items-center.h-screen
    (oops (str "Smilefjes for " (:tilsynsobjekt/navn spisested) " er snart tilbake"))]))

(defn render-page [_ctx page]
  (case (:page/kind page)
    :page.kind/spisested
    (render-spisested page)

    (layout
     [:div.grid.place-items-center.h-screen
      (oops "Smilefjes er snart tilbake")])))
