(ns smilefjes.ui.actions
  (:require [clojure.string :as str]
            [clojure.walk :as walk]
            [smilefjes.ui.search :as search]))

(defn interpolate-event-data [event data]
  (walk/postwalk
   (fn [x]
     (cond
       (= :event/key x)
       (.-key event)

       (= :event/target-value x)
       (some-> event .-target .-value)

       :else x))
   data))

(defn format-query-string [query-params]
  (when (not-empty query-params)
    (->> (for [[k v] query-params]
           (str k "=" v))
         (str/join "&")
         (str "?"))))

(defn perform-actions [state actions]
  (->> (remove nil? actions)
       (mapcat
        (fn [[action & args]]
          (println "[perform-actions]" action (pr-str args))
          (case action
            :action/assoc-in
            [{:kind ::assoc-in
              :args args}]

            :action/navigate
            [{:kind ::go-to-location
              :location (first args)}]

            :action/update-location
            (let [[path query-params] args]
              [{:kind ::assoc-in
                :args [[:location] {:params query-params}]}
               {:kind ::replace-state
                :location (str path (format-query-string query-params))}])

            :action/search
            (let [[q] args
                  path [:search :results q]]
              (when-not (get-in state path)
                [{:kind ::assoc-in
                  :args [path (vec (search/search-spisesteder state q))]}])))))))

(defn assoc-in* [m args]
  (reduce
   (fn [m [path v]]
     (assoc-in m path v))
   m
   (partition 2 args)))

(defn execute! [store effects]
  (doseq [[kind fx] (->> (remove nil? effects)
                         (group-by :kind))]
    (case kind
      ::assoc-in (swap! store assoc-in* (mapcat :args fx))
      ::go-to-location (set! js/window.location (:location (first fx)))
      ::replace-state (js/history.replaceState nil nil (:location (first fx))))))
