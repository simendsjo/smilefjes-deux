(ns smilefjes.dev
  (:require [clojure.tools.namespace.repl :as repl]
            [datomic-type-extensions.api :as d]
            [powerpack.dev :as dev]
            [smilefjes.core :as smilefjes]))

(defmethod dev/configure! :default []
  (repl/set-refresh-dirs "src" "dev" "test")
  (smilefjes/create-app :dev))

(defn start []
  (set! *print-namespace-maps* false)
  (dev/start))

(comment
  (def db (d/db (:datomic/conn (dev/get-app))))

  (d/q '[:find (count ?e)
         :where
         [?e :tilsynsbesøk/id]]
       db)

  (start)
  (dev/reset)

  )
