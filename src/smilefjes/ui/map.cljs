(ns smilefjes.ui.map)

(defn render-map [el]
  (let [map (js/mapboxgl.Map.
             #js {:container el
                  :style "mapbox://styles/mapbox/streets-v12"
                  :center #js [10.81 59.78]
                  :zoom 7})]
    (.on map "load"
         (fn []
           (.loadImage map "/images/map-marker.png"
                       (fn [_error image]
                         (.addImage map "custom-marker" image);
                         (.addSource map "points"
                                     (clj->js
                                      {:type "geojson"
                                       :data  {:type "FeatureCollection"
                                               :features [{:type "Feature"
                                                           :geometry {:type "Point"
                                                                      :coordinates [10.812988060061402 59.78772952219009]}
                                                           :properties {:title "Christian"
                                                                        :description "Her bor'n"}}
                                                          {:type "Feature"
                                                           :geometry {:type "Point"
                                                                      :coordinates [10.950073340056957 59.215469737612594]}
                                                           :properties {:title "Magnar"
                                                                        :description "Her er'n"}}]}}))

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

(defn receive-file [box e]
  (.preventDefault e)
  (.remove (.-classList box) "border-furu-600")
  (js/console.log (-> e .-dataTransfer .-files))

  (let [file (-> e .-dataTransfer .-files (aget 0))]
    (when file
      (let [reader (js/FileReader.)]
        (set! (.-onload reader)
              (fn [event]
                (let [contents (-> event .-target .-result)]
                  (js/console.log "Contents of the file:" contents))))
        (.readAsText reader file))))

)

(defn boot [el]
  (let [box (.-firstChild el)]
    (.addEventListener box "dragenter" #(.add (.-classList box) "border-furu-600"))
    (.addEventListener box "dragleave" #(.remove (.-classList box) "border-furu-600"))
    (.addEventListener box "dragover" (fn [e]
                                        (.preventDefault e)
                                        (set! (-> e .-dataTransfer .-dropEffect) "copy")))
    (.addEventListener box "drop" #(receive-file box %))))
