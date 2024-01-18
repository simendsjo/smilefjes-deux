(ns smilefjes.ui.search
  (:require [clojure.string :as str]
            [smilefjes.search :as search]
            [smilefjes.ui.dom :as dom]
            [smilefjes.ui.query-engine :as qe]))

(defonce search-engine (atom {:index-status :pending}))

(defn lookup-food [{:keys [names]} q]
  (when (get names q)
    [{:id q}]))

(defn search-spisesteder
  ([q] (search-spisesteder @search-engine q))
  ([engine q]
   (for [match
         (concat
          (lookup-food engine q)
          (qe/query
           (:index engine)
           {:queries [;; "Autocomplete" what the user is typing
                      (-> (:spisestedNavnEdgegrams (:schema engine))
                          (assoc :q q)
                          (assoc :fields ["spisestedNavnEdgegrams"])
                          (assoc :boost 10))
                      ;; Boost exact matches
                      (-> (:spisestedNavn (:schema engine))
                          (assoc :q q)
                          (assoc :fields ["spisestedNavn" "postnummer" "poststed"])
                          (assoc :boost 10))
                      ;; Add fuzziness
                      (-> (:spisestedNavnNgrams (:schema engine))
                          (merge {:q q
                                  :fields ["spisestedNavnNgrams" "poststedNgrams"]
                                  :operator :or
                                  :min-accuracy 0.9}))]
            :operator :or}))]
     (let [[url navn adr1 adr2 zip city] (get (:lookup engine) (js/parseInt (:id match) 10))]
       (-> match
           (assoc :url url)
           (assoc :title navn)
           (assoc :description (str adr1 (when (not-empty adr2) (str " " adr2)) ", "
                                    zip " " city)))))))

(defn load-json [url]
  (-> (js/fetch url)
      (.then #(.text %))
      (.then #(js->clj (js/JSON.parse %)))))

(defn initialize-search-engine []
  (when-not (:schema @search-engine)
    (swap! search-engine assoc :schema search/schema))
  (when (#{:pending :error} (:index-status @search-engine))
    (swap! search-engine assoc :index-status :loading)
    (-> (load-json (str "/search/index/nb.json"))
        (.then #(swap! search-engine assoc
                       :index (get % "index")
                       :lookup (get % "lookup")
                       :index-status :ready))
        (.catch (fn [e]
                  (js/console.error e)
                  (swap! search-engine assoc :index-status :error))))))

(defn waiting? []
  (#{:pending :loading} (:index-status @search-engine)))

(defn format-result [{:keys [loading? title url description]}]
  (str "<li class=\"mb-2 bg-slate-100 p-2 ac-result\">"
       (if loading?
         dom/loader
         (str "<a href=\"" url "\">"
              "<div class=\"font-bold\">" title "</div>"
              "<div class=\"text-sm\">" description "</div>"
              "</a>"))
       "</li>"))

(defn render-results [list-el results]
  (if (seq results)
    (do
      (->> (str/join (map format-result results))
           (set! (.-innerHTML list-el)))
      (dom/remove-class list-el "hidden"))
    (do
      (dom/add-class list-el "hidden")
      (set! (.-innerHTML list-el) ""))))

(defn handle-autocomplete-input-event [e list]
  (let [q (.-value (.-target e))
        n (or (some-> (.-target e) (.getAttribute "data-suggestions") js/parseInt) 10)]
    (if (< (.-length q) 3)
      (render-results list nil)
      (if (waiting?)
        (do (render-results list [{:loading? true}])
            (add-watch search-engine ::waiting-for-load
                       #(when-not (waiting?)
                          (remove-watch search-engine ::waiting-for-load)
                          (handle-autocomplete-input-event e list))))
        (render-results list (take n (search-spisesteder @search-engine q)))))))

(defn get-target-element [results selected d]
  (when (< 0 (.-length results))
    (cond
      (and selected (= :down d))
      (.-nextSibling selected)

      (and selected (= :up d))
      (.-previousSibling selected)

      (= :down d)
      (aget results 0)

      (= :up d)
      (aget results (dec (.-length results))))))

(defn navigate-results [element d]
  (let [selected (dom/qs element ".ac-selected")
        target-element (get-target-element (.querySelectorAll element ".ac-result") selected d)]
    (when target-element
      (when selected
        (.remove (.-classList selected) "ac-selected")
        (.remove (.-classList selected) "bg-slate-200"))
      (.add (.-classList target-element) "ac-selected")
      (.add (.-classList target-element) "bg-slate-200"))))

(defn handle-autocomplete-key-event [e element]
  (case (.-key e)
    "ArrowUp" (navigate-results element :up)
    "ArrowDown" (navigate-results element :down)
    nil))

(defn handle-autocomplete-submit-event [e]
  (when-let [selected (.querySelector (.-target e) ".ac-selected a")]
    (.preventDefault e)
    (set! js/window.location (.-href selected))))

(defn initialize-autocomplete [dom-element initial-query]
  (when-let [input (dom/qs dom-element "input")]
    (let [list (dom/qs dom-element ".js-suggestions")]
      (.addEventListener dom-element "input" #(handle-autocomplete-input-event % list))
      (.addEventListener dom-element "keyup" #(handle-autocomplete-key-event % list))
      (when-let [form (.closest dom-element "form")]
        (.addEventListener form "submit" #(handle-autocomplete-submit-event %)))
      (when (and initial-query (empty? (.-value input)))
        (set! (.-value input) initial-query))
      (when (seq (.-value input))
        (handle-autocomplete-input-event #js {:target input} list)))))
