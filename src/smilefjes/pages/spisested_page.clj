(ns smilefjes.pages.spisested-page
  (:require [smilefjes.icons :as icons]
            [smilefjes.plakaten :as plakaten]
            [smilefjes.ui :as ui]))

(defn zero-pad [n]
  (if (< n 10) (str "0" n) n))

(defn formater-dato [dato]
  (str (zero-pad (.getDayOfMonth dato)) "."
       (zero-pad (.getMonthValue dato)) "."
       (.getYear dato)))

(def karakter->smil-icon
  {"0" icons/smilefjes
   "1" icons/smilefjes
   "2" icons/strekmunn
   "3" icons/surmunn})

(defn vis-siste-tilsynsresultat [besøk]
  (let [karakter (:tilsynsbesøk/smilefjeskarakter besøk)]
    [:div.bg-white.rounded-lg.border.border-furu-500.px-5.py-2.text-center
     [:h2.text-l.flex-1 "Siste tilsynsresultat:"]
     [:div.w-36.my-2 {:title (str "Spisestedet har fått " (plakaten/beskriv-karakter karakter) ".")}
      (karakter->smil-icon karakter)]
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
    [:div.p-1.text-center.flex.flex-col.items-center.rounded-lg.cursor-pointer
     {:data-select_element_id (:tilsynsbesøk/id besøk)
      :data-selected_class "mmm-mini-selected"}
     [:div.w-8.my-2 {:title (str "Spisestedet har fått " (plakaten/beskriv-karakter karakter) ".")}
      (karakter->smil-icon karakter)]
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
  [:div.border-b-2.border-granskog-800
   (for [hovedvurdering (hent-vurderinger-av-hovedområdene besøk)]
     [:div.border-t-2.border-granskog-800
      [:div.bg-gåsunge-300.py-6.px-4.text-xl.flex.items-center
       [:div (vis-karakter-indikator (:vurdering/karakter hovedvurdering))]
       [:div (:kravpunkt/navn (:vurdering/kravpunkt hovedvurdering))]]
      (let [vurderinger (hent-vurderinger-for-hovedområde besøk (:vurdering/kravpunkt hovedvurdering))]
        (for [vurdering vurderinger]
          (let [irrelevant? (#{"4" "5"} (:vurdering/karakter vurdering))]
            [:div (when irrelevant? {:class "irrelevant-vurdering"})
             [:div.bg-white.py-4.px-4.border-b-2.border-gåsunge-200.flex.items-center
              [:div (vis-karakter-indikator (:vurdering/karakter vurdering))]
              [:div (when irrelevant?
                      {:class ["opacity-50"]})
               [:div (:kravpunkt/navn (:vurdering/kravpunkt vurdering))]
               [:div.text-xs (hent-vurderingstekst vurdering forrige-besøk)]]]])))])])

(defn checkbox [{:keys [toggle-class label]}]
  [:div.px-5
   [:label.mmm-checkbox {:data-toggle_body_class toggle-class}
    [:input {:type "checkbox"}]
    [:svg.mmm-svg.checkbox-marker
     {:xmlns "http://www.w3.org/2000/svg"
      :viewBox "0 0 24 24"}
     [:rect {:x "0.5"
             :y "0.5"
             :width "23"
             :height "23"
             :rx "3.5"}]
     [:svg {:x 5 :y 5}
      [:path {:d "M1.82609 4.97933L0 7.36562L6.05115 12.5999L14 3.3002L12.078 1.3999L6.06382 8.86295L1.82609 4.97933Z"
              :fill "white"
              :stroke "none"}]]]
    label]])

(defn render [ctx spisested]
  (let [besøkene (->> (:tilsynsbesøk/_tilsynsobjekt spisested)
                      (sort-by :tilsynsbesøk/dato)
                      reverse)]
    (ui/with-layout ctx
      (ui/header)
      [:div.bg-lysegrønn
       [:div.max-w-screen-md.mx-auto.p-5
        [:div.flex
         [:div.flex-1.js-select-element-parent
          (vis-spisested-info spisested)
          [:p.mt-5 "Tilsynsresultater:"]
          [:div.flex.gap-3.md:gap-5
           (map vis-mini-tilsynsresultat (take 4 besøkene))]
          (when-let [resten (seq (drop 4 besøkene))]
            [:div
             [:div.gamle-tilsyn
              (for [besøkene (partition-all 4 resten)]
                [:div.flex.gap-3.md:gap-5.mt-2
                 (map vis-mini-tilsynsresultat besøkene)])]
             [:div.text-xs.mt-2.vis-gamle-tilsyn-lenke
              [:span.underline.cursor-pointer
               {:data-toggle_body_class "vis-gamle-tilsyn"}
               "Se flere tilsynsresultater"]]])]
         [:div.hidden.md:block (vis-siste-tilsynsresultat (first besøkene))]]]]
      [:div.bg-gåsunge-200
       [:div.max-w-screen-md.mx-auto.py-5
        [:h2.text-2xl.px-5 "Vurdering"]
        [:div
         (for [[besøk forrige-besøk] (partition-all 2 1 besøkene)]
           [:div {:class (when (not= besøk (first besøkene)) "hidden")
                  :id (:tilsynsbesøk/id besøk)}
            [:p.my-2.px-5 (plakaten/oppsummer-smilefjeskarakter (:tilsynsbesøk/smilefjeskarakter besøk))]
            [:div.md:px-5.mt-10 (vis-vurderingsoversikt besøk forrige-besøk)]])]
        [:div.px-5.my-5 (checkbox {:toggle-class "vis-irrelevavnte-vurderinger"
                                   :label "Vis alle kravpunkter"})]
        [:p.px-5.my-10.text-sm
         "Mattilsynet har kontrollert etterlevelsen av sentrale krav i matlovgivningen. Resultatene baserer seg på observasjonene som ble gjort og de opplysningene som ble gitt under inspeksjonen."]]])))
