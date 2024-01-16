(ns smilefjes.core
  (:require [smilefjes.ingest :as ingest]
            [smilefjes.pages :as pages]))

(defn create-app [env]
  (cond-> {:site/default-locale :no
           :site/title "Smilefjes"

           :optimus/bundles {"app.css"
                             {:public-dir "public"
                              :paths ["/tailwind-out.css"]}}

           :powerpack/build-dir "docker/build"
           :powerpack/content-dir "content"
           :powerpack/source-dirs ["src" "dev"]
           :powerpack/resource-dirs ["resources"]
           :powerpack/content-file-suffixes ["md" "edn" "csv"]
           :powerpack/port 5055
           :powerpack/log-level :debug
           :powerpack/render-page #'pages/render-page
           :powerpack/on-ingested #'ingest/on-ingested
           :m1p/dictionaries {:nb ["src/smilefjes/i18n/nb.edn"]
                              :nn ["src/smilefjes/i18n/nn.edn"]}}
    (= :build env)
    (assoc :site/base-url "https://smilefjes.mattilsynet.no")

    (= :dev env) ;; serve figwheel compiled js
    (assoc :powerpack/dev-assets-root-path "public")))
