(ns smilefjes.ui.search
  (:require [clojure.string :as str]
            [smilefjes.search :as search]
            [smilefjes.ui.dom :as dom]
            [smilefjes.ui.query-engine :as qe]))

(defn search-spisesteder [engine q]
  (for [match
        (qe/query
         (::index engine)
         {:queries [;; "Autocomplete" what the user is typing
                    (-> (:spisestedNavnEdgegrams (::schema engine))
                        (assoc :q q)
                        (assoc :fields ["spisestedNavnEdgegrams"])
                        (assoc :boost 10))
                    ;; Boost exact matches
                    (-> (:spisestedNavn (::schema engine))
                        (assoc :q q)
                        (assoc :fields ["spisestedNavn" "postnummer" "poststed"])
                        (assoc :boost 10))
                    ;; Add fuzziness
                    (-> (:spisestedNavnNgrams (::schema engine))
                        (merge {:q q
                                :fields ["spisestedNavnNgrams" "poststedNgrams"]
                                :operator :or
                                :min-accuracy 0.9}))]
          :operator :or})]
    (let [[url navn adr1 adr2 zip city] (get (::lookup engine) (js/parseInt (:id match) 10))]
      (-> match
          (assoc :url url)
          (assoc :title navn)
          (assoc :description (str adr1 (when (not-empty adr2) (str " " adr2)) ", "
                                   zip " " city))))))

(defn load-json [url]
  (-> (js/fetch url)
      (.then #(.text %))
      (.then #(js->clj (js/JSON.parse %)))))

(defn pending? [state]
  (or (not (contains? state ::status))
      (#{:pending :error} (::status state))))

(defn initialize-search-engine [store]
  (when-not (::schema @store)
    (swap! store assoc ::schema search/schema))
  (when (pending? @store)
    (swap! store assoc ::status :loading)
    (-> (load-json (str "/search/index/nb.json"))
        (.then #(swap! store assoc
                       ::index (get % "index")
                       ::lookup (get % "lookup")
                       ::status :ready))
        (.catch (fn [e]
                  (js/console.error e)
                  (swap! store assoc ::status :error))))))
