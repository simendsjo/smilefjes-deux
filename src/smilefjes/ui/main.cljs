(ns smilefjes.ui.main
  (:require [replicant.dom :as replicant]
            [smilefjes.components.autocomplete :as ac]
            [smilefjes.components.search-result :as sr]
            [smilefjes.ui.actions :as actions]
            [smilefjes.ui.body-toggles :as body-toggles]
            [smilefjes.ui.dom :as dom]
            [smilefjes.ui.search :as search-ui]
            [smilefjes.ui.select-element :as select-element]
            [smilefjes.ui.tracking :as tracking]))

(defonce store
  (atom {:location (let [params (dom/get-params)]
                     (cond-> {}
                       (not-empty params) (assoc :params params)))}))

(def views
  {"autocomplete" {:component #'ac/Autocomplete
                   :prepare #'ac/prepare}
   "search-form" {:component #'ac/Autocomplete
                  :prepare #'ac/prepare-search}
   "search-result" {:component #'sr/SearchResult
                    :prepare #'sr/prepare
                    :boot-actions #'sr/get-boot-actions}})

(defn get-view [store el]
  (when-let [{:keys [component prepare boot-actions]}
             (get views (.getAttribute el "data-view"))]
    (cond-> {:el el :component #(component (prepare %))}
      boot-actions (assoc :boot-actions boot-actions))))

(defn render [views state]
  (doseq [{:keys [el component]} views]
    (replicant/render el (component state))))

(defn handle-event [_rd event actions]
  (->> (actions/interpolate-event-data event actions)
       (actions/perform-actions @store)
       (actions/execute! store)))

(defn get-replicant-views []
  (->> (dom/qsa ".replicant-root")
       (keep
        (fn [el]
          (if-let [view (get-view store el)]
            (do
              (set! (.-innerHTML (:el view)) "")
              view)
            (js/console.error "Replicant root has no recognized data-view attribute" el))))
       doall))

(defn boot []
  (tracking/track-page-view)
  (.addEventListener js/document.body "click" body-toggles/handle-clicks)
  (.addEventListener js/document.body "click" select-element/handle-clicks)

  (when-let [views (get-replicant-views)]
    (add-watch store ::render (fn [_ _ _ state] (render views state)))
    (replicant/set-dispatch! #'handle-event)
    (search-ui/initialize-search-engine
     store
     (fn []
       (println "Search engine available, running boot actions")
       (doseq [get-boot-actions (keep :boot-actions views)]
         (->> (get-boot-actions @store)
              (actions/perform-actions @store)
              (actions/execute! store))))))

  (swap! store assoc :booted-at (.getTime (js/Date.))))

(defonce ^:export kicking-out-the-jams (boot))
