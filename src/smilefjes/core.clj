(ns smilefjes.core
  (:require [smilefjes.ingest :as ingest]
            [smilefjes.pages :as pages]))

(defn get-context []
  {:matomo/site-id "9"})

(defn create-app [env]
  (cond-> {:site/default-locale :no
           :site/title "Smilefjes"

           :optimus/bundles {"app.css"
                             {:public-dir "public"
                              :paths ["/tailwind-out.css"]}

                             "/app.js"
                             {:public-dir "public"
                              :paths ["/js/compiled/app.js"]}}

           :optimus/assets [{:public-dir "public"
                             :paths [#"/images/*.*"]}]

           :powerpack/build-dir "docker/build"
           :powerpack/content-dir "content"
           :powerpack/source-dirs ["src" "dev"]
           :powerpack/resource-dirs ["resources"]
           :powerpack/port 5055
           :powerpack/log-level :debug
           :powerpack/render-page #'pages/render-page
           :powerpack/on-started #'ingest/on-started
           :powerpack/get-context #'get-context}
    ;; (= :build env)
    ;; (assoc :site/base-url "https://smilefjes.mattilsynet.no")

    (= :dev env) ;; serve figwheel compiled js
    (assoc :powerpack/dev-assets-root-path "public")))
