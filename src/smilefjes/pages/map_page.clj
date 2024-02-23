(ns smilefjes.pages.map-page
  (:require [smilefjes.layout :as layout]))

(defn render-page [ctx page]
  [:html
   [:head
    (layout/get-standard-head-elements ctx page)
    [:link {:rel "stylesheet" :href "https://api.mapbox.com/mapbox-gl-js/v2.14.1/mapbox-gl.css"}]]
   [:body
    [:script {:src "https://api.mapbox.com/mapbox-gl-js/v2.14.1/mapbox-gl.js"}]
    [:script {:type "text/javascript"} "mapboxgl.accessToken = 'pk.eyJ1IjoiY3JvbWxlY2giLCJhIjoiY2xzd3dqcTNsMW9sYzJzczA5N2R1enpsZSJ9.tcr8dy_CopvtvJEzapcahA';"]
    (layout/header)
    [:div.min-h-screen.flex.flex-col.justify-between
     [:div.grow.relative {:id "mapbox"}
      [:div.border-dashed.border-2.border-furu-400.bg-furu-100.rounded.absolute.top-8.right-8.bottom-8.left-8.flex.items-center.justify-center.transition
       [:p.mb-4 "Slipp CSV-filen her for å se stedene"]]]
     (layout/footer)]
    (layout/get-tracking-pixel ctx)]])
