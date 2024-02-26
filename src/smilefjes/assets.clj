(ns smilefjes.assets
  (:require [optimus.assets :as assets]
            [optimus.optimizations :as optimizations]
            [powerpack.assets :as powerpack-assets]
            [smilefjes.core :as smilefjes]))

(defn bool-property [prop default]
  (if-let [property (System/getProperty prop)]
    (Boolean/parseBoolean property)
    default))

(def optimize? (bool-property "optimus.assets.optimize" true))

(def assets
  (if optimize?
    (-> (powerpack-assets/load-assets (smilefjes/create-app :build))
        (optimizations/all {}))
    (powerpack-assets/load-assets (smilefjes/create-app :dev))))

(defn preferred-path [path]
  (->> assets
       (filter #(= path (assets/original-path %)))
       (remove :outdated)
       first
       :path))

(defmacro asset-path [path]
  (preferred-path path))
