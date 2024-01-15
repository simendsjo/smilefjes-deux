(ns smilefjes.core
  (:require [smilefjes.pages :as pages]))

(defn create-app [env]
  (cond-> {:site/default-locale :no
           :site/title "Smilefjes"

           :powerpack/build-dir "docker/build"
           :powerpack/content-dir "content"
           :powerpack/source-dirs ["src" "dev"]
           :powerpack/resource-dirs ["resources"]
           :powerpack/port 5055
           :powerpack/render-page #'pages/render-page
           :m1p/dictionaries {:nb ["src/smilefjes/i18n/nb.edn"]
                              :nn ["src/smilefjes/i18n/nn.edn"]}}
    (= :build env)
    (assoc :site/base-url "https://smilefjes.mattilsynet.no")

    (= :dev env) ;; serve figwheel compiled js
    (assoc :powerpack/dev-assets-root-path "public")))
