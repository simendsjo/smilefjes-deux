(ns smilefjes.dev
  (:require [clojure.tools.namespace.repl :as repl]
            [datomic-type-extensions.api :as d]
            [powerpack.dev :as dev :refer [reset start]]
            [smilefjes.core :as smilefjes]))

(defmethod dev/configure! :default []
  (set! *print-namespace-maps* false)
  (repl/set-refresh-dirs "src" "dev" "test")
  (smilefjes/create-app :dev))

(comment
  (def app-db (d/db (:datomic/conn (dev/get-app))))

  (start)
  (reset)

  )
