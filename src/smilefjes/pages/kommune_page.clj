(ns smilefjes.pages.kommune-page
  (:require [datomic-type-extensions.api :as d]
            [smilefjes.layout :as layout]))

(defn render [ctx page]
  (layout/with-layout ctx page
    (layout/header)
    [:div.bg-lysegrønn
     [:div.max-w-screen-md.mx-auto.p-5
      [:h1 (:kommune/navn page)]]]))

(comment
  (require 'smilefjes.dev)
  (def page (d/entity smilefjes.dev/db [:kommune/kode "3107"]))

  ;; Alle spisesteder
  (->> page :poststed/_kommune (mapcat :spisested/_poststed))

  )
