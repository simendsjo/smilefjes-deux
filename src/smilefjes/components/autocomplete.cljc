(ns smilefjes.components.autocomplete
  (:require [fontawesome.icons :as icons]
            #?(:cljs [replicant.core :as r])))

(def button-sizes
  {:large :mmm-button-large
   :small :mmm-button-small})

(defn Button [{:keys [href text inline? secondary? icon icon-position size] :as attrs}]
  [(if href :a.mmm-button.mmm-focusable :button.mmm-button.mmm-focusable)
   (cond-> (dissoc attrs :inline? :size :text :secondary? :icon :icon-position)
     inline? (update :class conj :mmm-button-inline)
     secondary? (update :class conj :mmm-button-secondary)
     (button-sizes size) (update :class conj (button-sizes size)))
   (when (and icon (not= :after icon-position))
     (icons/render icon {:class :mmm-button-icon}))
   [:span text]
   (when (and icon (= :after icon-position))
     (icons/render icon {:class :mmm-button-icon}))])

(def text-input-sizes
  {:small :mmm-input-compact})

(defn TextInput [attrs]
  (let [size-class (text-input-sizes (:size attrs))]
    [:input.mmm-input.mmm-focusable
     (cond-> (dissoc attrs :size)
       size-class
       (update :class #(if (coll? %)
                         (conj % size-class)
                         [% size-class])))]))

(def autocomplete-sizes
  {:small :mmm-search-input-compact})

(defn dispatch-keyboard-event [e key-actions]
  #?(:cljs
     (when-let [actions (get key-actions (.-key e))]
       (.preventDefault e)
       (.stopPropagation e)
       (r/*dispatch* {:replicant/event :replicant.event/dom-event} e actions))))

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
        [:li.p-4.w-full.mx-auto
         [:div.animate-pulse.flex.space-x-8.items-center
          [:div.flex-1.space-y-4.py-1
           [:div.h-2.bg-slate-500.rounded]
           [:div.space-y-1
            [:div.h-2.bg-slate-500.rounded]]]
          [:div.rounded-full.bg-slate-500.h-8.w-8]]])
      (for [suggestion suggestions]
        [:li.p-2.5.cursor-pointer.hover:bg-furu-400
         {:on {:click (:actions suggestion)}
          :class [(when (:current? suggestion)
                    "bg-furu-400")
                  (when (:zebra? suggestion)
                    "bg-lysegrønn")]}
         [:a {:href (:href suggestion)}
          [:div (:title suggestion)]
          [:div.text-sm (:description suggestion)]]])])])

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

(defn prepare [state]
  (let [query (or (get-in state q-path) (get-in state [:location :params "q"]) "")
        current (get-in state current-path)
        suggestions (get-in state [:search :results query])
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
                         "Enter" (if current
                                   [[:action/navigate (get-in suggestions [current :url])]]
                                   [[:action/navigate (str "/sok/?q=" query)]])})}
     :loading? (#{:pending :error} (:smilefjes.ui.search/status state))
     :button {:text "Søk"
              :icon (icons/icon :fontawesome.solid/magnifying-glass)}
     :suggestions (->> suggestions
                       (take 5)
                       (map-indexed
                        (fn [idx s]
                          (if (= current idx)
                            (assoc s :current? true)
                            (assoc s :zebra? (= 1 (mod idx 2))))))
                       (map #(assoc % :actions [[:action/navigate (:url %)]])))}))
