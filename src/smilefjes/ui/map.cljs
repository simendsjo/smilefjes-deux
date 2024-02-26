(ns smilefjes.ui.map
  (:require [clojure.string :as str])
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
                                               :features features}}))
                         (.addLayer map (clj->js
                                         {:id "points"
                                          :type "symbol"
                                          :source "points"
                                          :layout {:icon-image "custom-marker"
                                                   :text-field ["get" "title"]
                                                   :text-font ["Open Sans Semibold"]
                                                   :text-offset [0 0.5]
                                                   :text-anchor "top"}}))
                         (.on map "click"
                              "points"
                              (fn [e]
                                (let [features (.queryRenderedFeatures map (.-point e))]
                                  (when (seq features)
                                    (let [feature (first features)
                                          properties (.-properties feature)
                                          coordinates (.-geometry feature)
                                          popup (js/mapboxgl.Popup.
                                                 #js {:closeButton true
                                                      :offset [0, 10]})
                                          description (.-description properties)]
                                      (.setLngLat popup (.-coordinates coordinates))
                                      (.setHTML popup description)
                                      (.addTo popup map))))))))))))

(defn parse-csv [s]
  (let [[headers & lines] (str/split-lines s)
        ks (map keyword (str/split headers #"\|"))]
    (for [line lines]
      (zipmap ks (str/split line #"\|")))))

(defn ->map-feature [virksomhet]
  {:type "Feature"
   :geometry {:type "Point"
              :coordinates [(:lon virksomhet) (:lat virksomhet)]}
   :properties {:title (:navn virksomhet)
                :description (str "<h3>" (:navn virksomhet) "</h3>"
                                  "<p>Orgnummer: " (:orgnr virksomhet) "</p>"
                                  "<p>" (:adresse virksomhet) ", " (:sted virksomhet) "</p>")}})

(defn receive-file [el box e]
  (.preventDefault e)
  (.remove (.-classList box) "border-furu-600")
  (let [file (-> e .-dataTransfer .-files (aget 0))]
    (when file
      (let [reader (js/FileReader.)]
        (set! (.-onload reader)
              (fn [event]
                (let [contents (-> event .-target .-result)]
                  (render-map el (map ->map-feature (parse-csv contents))))))
        (.readAsText reader file)))))

(defn boot [el]
  (let [box (.-firstChild el)]
    (.addEventListener box "dragenter" #(.add (.-classList box) "border-furu-600"))
    (.addEventListener box "dragleave" #(.remove (.-classList box) "border-furu-600"))
    (.addEventListener box "dragover" (fn [e]
                                        (.preventDefault e)
                                        (set! (-> e .-dataTransfer .-dropEffect) "copy")))
    (.addEventListener box "drop" #(receive-file el box %))))
