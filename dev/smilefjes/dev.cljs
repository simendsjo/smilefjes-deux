(ns ^:figwheel-hooks smilefjes.dev
  (:require [gadget.inspector :as inspector]
            [smilefjes.ui.main :as smilefjes]))

(inspector/inspect "App state" smilefjes/store)

(defn ^:after-load main []
  (swap! smilefjes/store assoc :reloaded-at (js/Date.)))
