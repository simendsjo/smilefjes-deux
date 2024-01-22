(ns smilefjes.pages.spisested-page
  (:require [smilefjes.plakaten :as plakaten]
            [smilefjes.ui :as ui]))

(defn formater-dato [dato]
  (str (.getDayOfMonth dato) "."
       (.getMonthValue dato) "."
       (.getYear dato)))

(defn vis-siste-tilsynsresultat [besøk]
  (let [karakter (:tilsynsbesøk/smilefjeskarakter besøk)]
    [:div.bg-white.rounded.border.border-furu-500.px-5.py-2.text-center
     [:h2.text-l.flex-1 "Siste tilsynsresultat:"]
     [:img.w-36.my-2 {:title (str "Spisestedet har fått " (plakaten/beskriv-karakter karakter) ".")
                      :src (plakaten/smilefjes-svg-url karakter)}]
     (formater-dato (:tilsynsbesøk/dato besøk))]))

(defn vis-spisested-info [spisested]
  (let [{:keys [linje1 linje2 poststed postnummer]} (:spisested/adresse spisested)]
    [:div
     [:h1.text-3xl (:spisested/navn spisested)]
     [:div linje1]
     [:div linje2]
     [:div postnummer " " poststed]]))

(defn vis-mini-tilsynsresultat [besøk]
  (let [karakter (:tilsynsbesøk/smilefjeskarakter besøk)]
    [:div.py-1.text-center.flex.flex-col.items-center
     [:img.w-8.my-2 {:title (str "Spisestedet har fått " (plakaten/beskriv-karakter karakter) ".")
                     :src (plakaten/smilefjes-svg-url karakter)}]
     [:div.text-xs (formater-dato (:tilsynsbesøk/dato besøk))]]))

(defn hent-vurderinger-av-hovedområdene [besøk]
  (->> (:tilsynsbesøk/vurderinger besøk)
       (remove (comp :kravpunkt/hovedområde :vurdering/kravpunkt))
       (sort-by (comp :kravpunkt/id :vurdering/kravpunkt))))

(defn hent-vurderinger-for-hovedområde [besøk kravpunkt]
  (->> (:tilsynsbesøk/vurderinger besøk)
       (filter (comp #{kravpunkt} :kravpunkt/hovedområde :vurdering/kravpunkt))
       (sort-by (comp :kravpunkt/id :vurdering/kravpunkt))))

(defn vis-karakter-indikator [karakter]
  (case karakter
    ("0" "1") [:img.w-7.mr-3 {:src "/images/checkmark.svg"}]
    ("2" "3") [:img.w-7.mr-3 {:src "/images/xmark.svg"}]
    [:div.w-7.mr-3]))

(defn hent-vurderingstekst [vurdering forrige-besøk]
  (let [forrige-vurdering (->> (:tilsynsbesøk/vurderinger forrige-besøk)
                               (filter #(= (:vurdering/kravpunkt vurdering)
                                           (:vurdering/kravpunkt %)))
                               first)]
    (if (and (#{"0" "1"} (:vurdering/karakter vurdering))
             (#{"2" "3"} (:vurdering/karakter forrige-vurdering)))
      "Regelverksbruddet som ble funnet ved forrige inspeksjon er fulgt opp og funnet i orden."
      ((-> vurdering :vurdering/kravpunkt :kravpunkt/karakter->tekst)
       (:vurdering/karakter vurdering)))))

(defn vis-vurderingsoversikt [besøk forrige-besøk]
  [:div.border-b-2.border-granskog-800.my-10
   (for [hovedvurdering (hent-vurderinger-av-hovedområdene besøk)]
     [:div.border-t-2.border-granskog-800
      [:div.bg-gåsunge-300.py-6.px-4.text-xl.flex.items-center
       [:div (vis-karakter-indikator (:vurdering/karakter hovedvurdering))]
       [:div (:kravpunkt/navn (:vurdering/kravpunkt hovedvurdering))]]
      (let [vurderinger (hent-vurderinger-for-hovedområde besøk (:vurdering/kravpunkt hovedvurdering))]
        (for [vurdering vurderinger]
          (let [ikke-interessant? (#{"4" "5"} (:vurdering/karakter vurdering))]
            [:div.bg-white.py-4.px-4.border-b-2.border-gåsunge-200.flex.items-center
             {:class (when ikke-interessant?
                       "not-interesting")}
             [:div (vis-karakter-indikator (:vurdering/karakter vurdering))]
             [:div (when ikke-interessant?
                     {:class ["opacity-50"]})
              [:div (:kravpunkt/navn (:vurdering/kravpunkt vurdering))]
              [:div.text-xs (hent-vurderingstekst vurdering forrige-besøk)]]])))])])

(defn render [ctx spisested]
  (let [besøkene (->> (:tilsynsbesøk/_tilsynsobjekt spisested)
                      (sort-by :tilsynsbesøk/dato)
                      reverse)
        besøk (first besøkene)
        forrige-besøk (second besøkene)]
    (ui/with-layout ctx
      (ui/header)
      [:div.bg-lysegrønn
       [:div.max-w-screen-md.mx-auto.p-5
        [:div.flex
         [:div.flex-1
          (vis-spisested-info spisested)
          [:p.mt-5 "Tilsynsresultater:"]
          [:div.flex.gap-5
           (map vis-mini-tilsynsresultat (take 4 besøkene))]]
         [:div.hidden.md:block (vis-siste-tilsynsresultat besøk)]]
        [:p.mt-5.text-xs "Mattilsynets smilefjestjeneste er nede på grunn av teknisk svikt, men vi jobber iherdig med å få på plass en erstatning. Nå har du snublet inn i vårt pågående arbeid. Kos med kaos!"]]]
      [:div.bg-gåsunge-200
       [:div.max-w-screen-md.mx-auto.py-5
        [:h2.text-2xl.px-5 "Vurdering"]
        [:p.my-2.px-5 (plakaten/oppsummer-smilefjeskarakter (:tilsynsbesøk/smilefjeskarakter besøk))]
        [:div.md:px-5 (vis-vurderingsoversikt besøk forrige-besøk)]]])))
