(ns smilefjes.pages)

(defn layout [& body]
  [:html
   [:body
    body]])

(defn render-spisested [spisested]
  (layout
   [:div.grid.place-items-center.h-screen
    [:div.max-w-md
     [:h1.text-xl "Smilefjes for " (:tilsynsobjekt/navn spisested) " er snart tilbake"]
     [:p.mt-5 "Den gamle tjenesten måtte dessverre hastenedlegges på grunn av sikkerhetshensyn, men vi jobber iherdig med å få satt opp en ny smilefjesoversikt."]]]))

(defn render-page [_ctx page]
  (case (:page/kind page)
    :page.kind/spisested
    (render-spisested page)

    (layout
     [:div.grid.place-items-center.h-screen
      [:div.max-w-md
       [:h1.text-xl "Smilefjes er snart tilbake"]
       [:p.mt-5 "Den gamle tjenesten måtte dessverre hastenedlegges på grunn av sikkerhetshensyn, men vi jobber iherdig med å få satt opp en ny smilefjesoversikt."]]])))
