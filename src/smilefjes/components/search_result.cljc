(ns smilefjes.components.search-result
  (:require [smilefjes.components.autocomplete :as ac]
            [smilefjes.ui.search :as search]))

(defn Result [{:keys [actions href title description zebra?]}]
  [:li.py-4.px-2.cursor-pointer.hover:bg-gåsunge-300
   {:on {:click actions}
    :class [(when zebra? "bg-gåsunge-200")]}
   [:a {:href href}
    [:div.underline.hover:no-underline title]
    [:div.text-sm description]]])

(defn SearchResult [{:keys [results loading?]}]
  [:div
   [:ol
    (if loading?
      ac/loader-skeleton
      (map Result results))]])

(defn prepare [state]
  (let [query (get-in state [:location :params "q"])
        results (->> (get-in state [:search :results query])
                     ac/prepare-suggestions)]
    {:loading? (search/loading? state)
     :results results}))

(defn get-boot-actions [state]
  (when-let [query (not-empty (get-in state [:location :params "q"]))]
    [[:action/search query]]))
