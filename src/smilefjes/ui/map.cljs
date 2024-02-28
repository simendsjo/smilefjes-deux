(ns smilefjes.ui.map
  (:require [fontawesome.icons :as icons]
            [smilefjes.ui.actions :as actions])
  (:require-macros [smilefjes.assets :refer [asset-path]]))

(defn render-map [el features]
  (let [map (js/mapboxgl.Map.
             #js {:container el
                  :style "mapbox://styles/mapbox/streets-v12"
                  :center #js [10.81 59.78]
                  :zoom 7})]
    (.on map "load"
         (fn []
           (.loadImage map (asset-path "/images/map-marker.png")
                       (fn [_error image]
                         (.addImage map "custom-marker" image);

                         (.addSource map "points"
                                     (clj->js
                                      {:type "geojson"
                                       :data  {:type "FeatureCollection"
                                               :features features}
                                       :cluster true
                                       :clusterMaxZoom 12
                                       :clusterRadius 50}))

                         (.addLayer map (clj->js
                                         {:id "clusters"
                                          :type "circle"
                                          :source "points"
                                          :filter '[has point_count]
                                          :paint {:circle-color '[step
                                                                  [get point_count]
                                                                  "#054449"
                                                                  10
                                                                  "#054449"]
                                                  :circle-radius '[step
                                                                   [get point_count]
                                                                   20
                                                                   10 ;; Hvis flere enn 10, så...
                                                                   25
                                                                   ]}}))

                         (.addLayer map (clj->js
                                         {:id "cluster-counts"
                                          :type "symbol"
                                          :source "points"
                                          :filter '[has point_count]
                                          :layout {:text-field '[get point_count_abbreviated]
                                                   :text-font ["Open Sans Semibold"]
                                                   :text-size 12}
                                          :paint {:text-color "#FAF6F3"}}))

                         (.addLayer map (clj->js
                                         {:id "points"
                                          :type "symbol"
                                          :source "points"
                                          :filter '[! [has point_count]]
                                          :layout {:icon-image "custom-marker"
                                                   :icon-allow-overlap true
                                                   :text-field ["get" "title"]
                                                   :text-font ["Open Sans Semibold"]
                                                   :text-offset [0 0.5]
                                                   :text-allow-overlap true
                                                   :text-anchor "top"}}))
                         (.on map "click"
                              "points"
                              (fn [e]
                                (let [features (.queryRenderedFeatures map (aget e "point"))]
                                  (when (seq features)
                                    (let [feature (first features)
                                          properties (aget feature "properties")
                                          coordinates (aget feature "geometry")
                                          popup (js/mapboxgl.Popup.
                                                 #js {:closeButton true
                                                      :offset [0, 10]})
                                          description (aget properties "description")]
                                      (.setLngLat popup (aget coordinates "coordinates"))
                                      (.setHTML popup description)
                                      (.addTo popup map))))))))))))

(defn ->map-feature [{:sted/keys [kortnavn navn orgnummer lat lon adresse]}]
  {:type "Feature"
   :geometry {:type "Point"
              :coordinates [lon lat]}
   :properties {:title kortnavn
                :description (str "<h3>" navn "</h3>"
                                  "<p>Orgnummer: " orgnummer "</p>"
                                  "<p>" adresse "</p>")}})

(defn prepare-file [file report]
  (if (:kart/tittel report)
    (-> report
        (assoc :kart/features (map ->map-feature (:kart/steder report)))
        (assoc :file/name (.-name file)))
    (throw (js/Error. "Det ser ut til å være feil filtype. Dobbelsjekk at det er en fil ment for denne løsningen. Den skal ha filendelsen .edn"))))

(defmethod actions/perform-action ::drop [_ [e]]
  (.preventDefault e)
  [{:kind :smilefjes.ui.actions/assoc-in
    :args [[::ready-to-drop?] false]}
   (when-let [file (-> e .-dataTransfer .-files (aget 0))]
     {:kind :smilefjes.ui.actions/read-file
      :file file
      :parser #(prepare-file file %)
      :path [::files]})])

(defn dragover [e]
  (.preventDefault e)
  (set! (-> e .-dataTransfer .-dropEffect) "copy"))

(defn prepare-launchpad [state]
  {:text "Slipp edn-filen her for å se stedene"
   :class (if (::ready-to-drop? state)
            ["border-furu-600"]
            ["border-furu-400"])
   :on {:dragenter [[:action/assoc-in [::ready-to-drop?] true]]
        :dragleave [[:action/assoc-in [::ready-to-drop?] false]]
        :dragover dragover
        :drop [[:action/prevent-default :event/event]
               [:action/assoc-in [::current-file] :event/target-file-name]
               [::drop :event/event]]}})

(defn get-current-file [state]
  (when (::current-file state)
    (get-in state [::files (::current-file state)])))

(defn prepare-menu [state current]
  (if (= 0 (count (::files state)))
    {:text (:kart/tittel current)}
    (cond-> {:text (:kart/tittel current)
             :icon (if (::menu-open? state)
                     (icons/icon :fontawesome.solid/caret-up)
                     (icons/icon :fontawesome.solid/caret-down))}
      (not (::menu-open? state))
      (assoc :actions [[:action/assoc-in [::menu-open?] true]])

      (::menu-open? state)
      (merge {:actions [[:action/assoc-in [::menu-open?] false]]
              :options (concat (for [file (sort-by :kart/tittel (vals (::files state)))]
                                 {:text (:kart/tittel file)
                                  :current? (= (:file/name file) (:file/name current))
                                  :actions [[:action/assoc-in [::current-file] (:file/name file)]
                                            [:action/assoc-in [::menu-open?] false]]})
                               [{:text "Last opp ny fil"
                                 :actions [[:action/assoc-in [::current-file] :event/target-file-name]
                                           [:action/assoc-in [::menu-open?] false]]}])}))))

(def sidebar-path [:transient :sidebar-open?])

(defn sidebar-open? [state]
  (get-in state sidebar-path))

(defn prepare-sidebar-button [state]
  (if (sidebar-open? state)
    {:text "Vis steder"
     :icon (icons/icon :fontawesome.solid/x)
     :actions [[:action/assoc-in sidebar-path false]]}
    {:text "Vis steder"
     :icon (icons/icon :fontawesome.solid/bars)
     :actions [[:action/assoc-in sidebar-path true]]}))

(defn prepare-sidebar [state]
  (when-let [current (when (sidebar-open? state)
                       (get-current-file state))]
    {:sections
     (for [{:sted/keys [navn orgnummer detaljer]} (sort-by :sted/navn (:kart/steder current))]
       (let [path [:transient orgnummer ::expanded?]]
         {:title navn
          :button (if (get-in state path)
                    {:icon (icons/icon :fontawesome.solid/caret-up)
                     :actions [[:action/assoc-in path false]]}
                    {:icon (icons/icon :fontawesome.solid/caret-down)
                     :actions [[:action/assoc-in path true]]})
          :content (if (get-in state path)
                     (concat
                      (list {:content [:p "Orgnummer: " orgnummer]})
                      (for [{:keys [tittel hiccup elementer]} detaljer]
                        (if hiccup
                          {:title tittel
                           :content hiccup}
                          {:title tittel
                           ;; Denne datastrukturen kunne vært noe mer
                           ;; gjennomtenkt...
                           :sections (for [{:keys [tittel hiccup]} elementer]
                                       {:title tittel
                                        :hiccup hiccup})})))
                     (list {:content [:p "Orgnummer: " orgnummer]}))}))}))

(defn prepare [state]
  (if-let [current (get-current-file state)]
    {:menu (prepare-menu state current)
     :description (str (:kart/beskrivelse current)
                       " Oppdatert " (:kart/dato current) ".")
     :map-data {:id [(:file/name current) (:kart/dato current)]
                :features (:kart/features current)}
     :buttons [(prepare-sidebar-button state)]
     :sidebar (prepare-sidebar state)}
    {:launchpad (prepare-launchpad state)
     :menu (prepare-menu state {:kart/tittel "Last opp ny fil"})}))

(defn get-freezer-data [state]
  (->> (keys state)
       (filter keyword?)
       (filter (comp #{"smilefjes.ui.map"} namespace))
       (select-keys state)))

(defn Map [{:keys [features id]}]
  [:div.grow
   {:replicant/key id
    :replicant/on-update
    (fn [{:replicant/keys [node life-cycle]}]
      (when (= :replicant/mount life-cycle)
        (render-map node features)))}])

(defn Menu [{:keys [text icon actions options]}]
  [:nav.shrink-0.relative.z-10
   [:button.flex.items-center.rounded.bg-gåsunge-200.px-2.py-1.border-2.gap-2.min-w-64.text-left
    {:on {:click actions}}
    [:span.font-bold.grow text]
    (when icon (icons/render icon {:size 16}))]
   (when options
     [:ol.absolute.rounded.rounded-t-none.bg-gåsunge-200.pt-1.border-2.left-0.right-0.border-t-0
      {:style {:margin-top "-4px"}}
      (for [{:keys [text actions]} (remove :current? options)]
        [:li
         [:button.text-left.w-full.px-2.py-2.border-t.text-sm.hover:bg-white
          {:on {:click actions}}
          text]])])])

(defn Toolbar [{:keys [menu description buttons]}]
  [:div.border-b-2.border-furu-700.flex.gap-4.items-center.px-4
   (Menu menu)
   [:p.text-sm.opacity-80.my-4 (or description " ")]
   (when (seq buttons)
     [:div.flex.gap-4
      (for [{:keys [text actions icon]} buttons]
        [:button
         {:title text
          :on {:click actions}}
         (icons/render icon {:size 16})])])])

(defn Sidebar [{:keys [sections]}]
  (when sections
    [:aside.absolute.top-0.bottom-0.right-0.w-72.bg-white.border-l.border-furu-700.z-10.overflow-y-auto
     {:style {:transform "translateX(0)"
              :transition "transform 0.25s ease-in"}
      :replicant/mounting {:style {:transform "translateX(100%)"}}
      :replicant/unmounting {:style {:transform "translateX(100%)"}}}
     (map-indexed
      (fn [idx {:keys [title button content]}]
        [:button.block.w-full.border-b.text-left.text-sm
         {:on {:click (:actions button)}
          :class (when (= 0 (mod idx 2))
                   "bg-sommerdag-100")}
         [:div.flex.items-center.justify-between.p-2
          [:span.font-bold title]
          (icons/render (:icon button) {:size 16})]
         [:div.p-2.text-xs.pt-0
          (for [{:keys [content title sections]} content]
            (concat
             (when title [[:h3.mt-4.mb-2.text-md.font-bold title]])
             (when content [content])
             (mapcat
              (fn [{:keys [title hiccup]}]
                (list [:h4.font-bold.mt-2.mb-1 title]
                      hiccup))
              sections)))]])
      sections)]))

(defn MapView [data]
  [:div.grow.flex.flex-col
   (Toolbar data)
   [:main.grow.flex.flex-col.relative
    (Map (:map-data data))
    (Sidebar (:sidebar data))]])

(defn Launchpad [{:keys [class text on]}]
  [:div.border-dashed.border-2.bg-furu-100.rounded.flex.items-center.justify-center.transition.grow.m-4
   {:class class
    :on on}
   [:p.mb-4 text]])

(defn render [data]
  (or
   (when (:map-data data)
     (MapView data))
   (when-let [launchpad (:launchpad data)]
     [:div.grow.flex.flex-col
      (when (:menu data)
        (Toolbar data))
      (Launchpad launchpad)])))
