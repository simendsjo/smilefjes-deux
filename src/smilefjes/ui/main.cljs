(ns smilefjes.ui.main
  (:require [replicant.dom :as replicant]
            [smilefjes.components.autocomplete :as ac]
            [smilefjes.ui.actions :as actions]
            [smilefjes.ui.body-toggles :as body-toggles]
            [smilefjes.ui.dom :as dom]
            [smilefjes.ui.search :as search-ui]
            [smilefjes.ui.select-element :as select-element]
            [smilefjes.ui.tracking :as tracking]))

(defonce store (atom {}))

(defn get-component [el]
  (case (.getAttribute el "data-view")
    "autocomplete" #(ac/Autocomplete (ac/prepare %))
    nil))

(defn render [views state]
  (doseq [[el component] views]
    (replicant/render el (component state))))

(defn handle-event [_rd event actions]
  (->> (actions/interpolate-event-data event actions)
       (actions/perform-actions @store)
       (actions/execute! store)))

(defn boot []
  (tracking/track-page-view)
  (.addEventListener js/document.body "click" body-toggles/handle-clicks)
  (.addEventListener js/document.body "click" select-element/handle-clicks)

  (replicant/set-dispatch! #'handle-event)
  (search-ui/initialize-search-engine store)

  (let [views (->> (dom/qsa ".replicant-root")
                   (map
                    (fn [el]
                      (if-let [component (get-component el)]
                        (do
                          (set! (.-innerHTML el) "")
                          [el component])
                        (js/console.error "Replicant root has no recognized data-view attribute" el)))))]
    (add-watch store ::render (fn [_ _ _ state] (render views state))))

  (swap! store assoc :location (let [params (dom/get-params)]
                                 (cond-> {}
                                   (not-empty params) (assoc :params params)))))

(defonce ^:export kicking-out-the-jams (boot))
