(ns smilefjes.export
  (:require [powerpack.export :as export]
            [smilefjes.core :as smilefjes]))

(defn ^:export export [& _args]
  (set! *print-namespace-maps* false)
  (export/export! (smilefjes/create-app :build)))
