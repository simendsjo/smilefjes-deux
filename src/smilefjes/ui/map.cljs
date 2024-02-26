(ns smilefjes.ui.map
  (:require [clojure.string :as str]
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
                                (let [features (.queryRenderedFeatures map (.-point e))]
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

(defn parse-csv [s]
  (let [[headers & lines] (str/split-lines s)
        ks (map keyword (str/split headers #"\|"))]
    (for [line lines]
      (zipmap ks (str/split line #"\|")))))

(defn ->map-feature [virksomhet]
  (let [navn-biter (->> (str/split (:navn virksomhet) #" ")
                        (map str/capitalize))]
    {:type "Feature"
     :geometry {:type "Point"
                :coordinates [(:lon virksomhet) (:lat virksomhet)]}
     :properties {:title (str/join " " (take 2 navn-biter))
                  :description (str "<h3>" (str/join " " navn-biter) "</h3>"
                                    "<p>Orgnummer: " (:orgnr virksomhet) "</p>"
                                    "<p>" (:adresse virksomhet) ", " (:sted virksomhet) "</p>")}}))

(defmethod actions/perform-action ::drop [_ [e]]
  (.preventDefault e)
  [{:kind :smilefjes.ui.actions/assoc-in
    :args [[::ready-to-drop?] false]}
   (when-let [file (-> e .-dataTransfer .-files (aget 0))]
     {:kind :smilefjes.ui.actions/read-file
      :file file
      :parser #'parse-csv
      :path [::drop-data]})])

(defn dragover [e]
  (.preventDefault e)
  (set! (-> e .-dataTransfer .-dropEffect) "copy"))

(defn prepare [state]
  {:map (when-let [data (::drop-data state)]
          {:features (map ->map-feature data)})
   :launchpad {:text "Slipp CSV-filen her for å se stedene"
               :class (if (::ready-to-drop? state)
                        "border-furu-600"
                        "border-furu-400")
               :on {:dragenter [[:action/assoc-in [::ready-to-drop?] true]]
                    :dragleave [[:action/assoc-in [::ready-to-drop?] false]]
                    :dragover dragover
                    :drop [[:action/prevent-default :event/event]
                           [::drop :event/event]]}}})

(defn Map [{:keys [features]}]
  [:div.absolute.top-0.right-0.bottom-0.left-0
   {:replicant/on-update
    (fn [{:replicant/keys [node]}]
      (render-map node features))}])

(defn render [data]
  (or
   (some-> data :map Map)
   (when-let [{:keys [class text on]} (:launchpad data)]
     [:div.border-dashed.border-2.bg-furu-100.rounded.absolute.top-8.right-8.bottom-8.left-8.flex.items-center.justify-center.transition
      {:class class
       :on on}
      [:p.mb-4 text]])))
