(ns ^:figwheel-hooks smilefjes.ui.main
  (:require [smilefjes.ui.dom :as dom]
            [smilefjes.ui.search :as search-ui]
            [smilefjes.ui.tracking :as tracking]))

(defn ^:after-load main []
  (search-ui/initialize-search-engine))

(defn boot []
  (main)
  (tracking/track-page-view)
  (search-ui/initialize-autocomplete
   (js/document.querySelector ".js-autocomplete")
   (get (dom/get-params) "q")))

(defonce ^:export kicking-out-the-jams (boot))
