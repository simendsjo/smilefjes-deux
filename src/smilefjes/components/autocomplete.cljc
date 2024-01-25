(ns smilefjes.components.autocomplete
  (:require [fontawesome.icons :as icons]
            [smilefjes.components.text-input :refer [TextInput]]
            [smilefjes.components.button :refer [Button]]
            [smilefjes.icons :as smil]
            [smilefjes.ui.search :as search]
            #?(:cljs [replicant.core :as r])))

(def autocomplete-sizes
  {:small :mmm-search-input-compact})

(defn dispatch-keyboard-event [e key-actions]
  #?(:cljs
     (when-let [actions (get key-actions (.-key e))]
       (.preventDefault e)
       (.stopPropagation e)
       (r/*dispatch* {:replicant/event :replicant.event/dom-event} e actions))))

(defn Suggestion [{:keys [actions current? zebra? href title description illustration]}]
  [:li.p-2.5.cursor-pointer.hover:bg-furu-400.flex.items-center
   {:on {:click actions}
    :class [(when current?
              "bg-furu-400")
            (when zebra?
              "bg-lav")]}
   [:a.grow {:href href}
    [:div.mb-1.5 title]
    [:div.text-sm description]]
   (when illustration
     [:div.basis-10.shrink-0 illustration])])

(def loader-skeleton
  [:li.p-4.w-full.mx-auto
   [:div.animate-pulse.flex.space-x-8.items-center
    [:div.flex-1.space-y-4.py-1
     [:div.h-2.bg-slate-500.rounded]
     [:div.space-y-1
      [:div.h-2.bg-slate-500.rounded]]]
    [:div.rounded-full.bg-slate-500.h-8.w-8]]])

(defn Autocomplete [{:keys [q class size actions button suggestions loading?]}]
  [:fieldset.mmm-search-input {:class [(autocomplete-sizes size) class]}
   [:div.mmm-action-input
    (TextInput
     {:value (or q "")
      :type "search"
      :size size
      :on {:input (:input actions)
           :keyup (when-let [key-actions (:keyup actions)]
                    #(dispatch-keyboard-event % key-actions))}
      :autocomplete "off"
      :aria-autocomplete "list"
      :aria-controls "suggestions"
      :aria-haspopup "menu"})
    (Button (assoc button :type "submit" :inline? true :size size))]
   (when loading?
     [:div.mmm-loader.absolute.top-3.right-24.z-10])
   (when (or (seq suggestions) loading?)
     [:ol.mmm-ac-results.bg-neutral-50 {:id "suggestions"}
      (when loading?
        loader-skeleton)
      (map Suggestion suggestions)])])

(defn AutocompleteSmall [params]
  (-> (assoc params :size :small)
      (update :button dissoc :icon)
      Autocomplete))

(defn get-up-n [n current]
  (cond
    (or (nil? current)
        (< n current)) (dec n)
    (= 0 current) nil
    :else (dec current)))

(defn get-down-n [n current]
  (let [current (or current -1)]
    (when (< current (dec n))
      (inc current))))

(def q-path [:autocomplete :q])
(def current-path [:autocomplete :current])

(defn prepare-suggestions
  ([suggestions] (prepare-suggestions nil suggestions))
  ([current suggestions]
   (->> suggestions
        (map-indexed
         (fn [idx s]
           (if (= current idx)
             (assoc s :current? true)
             (assoc s :zebra? (= 1 (mod idx 2))))))
        (map #(-> (assoc % :href (:url %))
                  (assoc :actions [[:action/navigate (:url %)]])
                  (assoc :illustration (get smil/karakter->smil (ffirst (:tilsyn %)))))))))

(defn get-location-query [state]
  (get-in state [:location :params "q"]))

(defn get-query [state]
  (or (get-in state q-path) (get-location-query state) ""))

(defn get-suggestions [state query]
  (get-in state [:search :results query]))

(defn prepare [state]
  (let [query (get-query state)
        current (get-in state current-path)
        suggestions (get-suggestions state query)
        res-n (count suggestions)]
    {:q query
     :actions {:input (concat
                       [[:action/assoc-in q-path :event/target-value]]
                       (when (< 1 (count query))
                         [[:action/search :event/target-value]
                          [:action/assoc-in current-path nil]]))
               :keyup (when (< 0 res-n)
                        {"ArrowUp" [[:action/assoc-in current-path (get-up-n res-n current)]]
                         "ArrowDown" [[:action/assoc-in current-path (get-down-n res-n current)]]
                         "Enter" (cond
                                   current
                                   [[:action/navigate (get-in suggestions [current :url])]]

                                   (= 1 res-n)
                                   [[:action/navigate (:url (first suggestions))]]

                                   (not-empty query)
                                   [[:action/navigate (str "/sok/?q=" query)]])})}
     :loading? (and (nil? suggestions)
                    (not-empty query)
                    (search/loading? state))
     :button {:text "Søk"
              :icon (icons/icon :fontawesome.solid/magnifying-glass)
              :on {:click [[:action/navigate (str "/sok/?q=" query)]]}}
     :suggestions (->> suggestions
                       (take 5)
                       (prepare-suggestions current))}))

(defn prepare-search [state]
  (let [query (get-query state)]
    {:q query
     :actions {:input [[:action/assoc-in q-path :event/target-value]]
               :keyup (when (not-empty query)
                        {"Enter" [[:action/search query]
                                  [:action/update-location "/sok/" {"q" :event/target-value}]]})}
     :button {:text "Søk"
              :icon (icons/icon :fontawesome.solid/magnifying-glass)
              :on (when-not (= query (get-location-query state))
                    {:click [[:action/search query]
                             [:action/update-location "/sok/" {"q" query}]]})}}))
