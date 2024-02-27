(ns smilefjes.ui.storage
  (:require [cljs.reader :as reader]))

(defn get-json [k]
  (try
    (some-> (js/localStorage.getItem k)
            js/JSON.parse)
    (catch :default e
      (js/console.error "Unable to read from local storage" e)
      nil)))

(defn get-json-edn [k]
  (some-> (get-json k) js->clj))

(defn set-json [k v]
  (try
    (some->> v
             js/JSON.stringify
             (js/localStorage.setItem k))
    (catch :default e
      (js/console.error "Unable to write to local storage" e)
      nil)))

(defn set-json-edn [k v]
  (some->> v clj->js (set-json k)))

(defn get-edn [k]
  (try
    (some-> (js/localStorage.getItem k)
            reader/read-string)
    (catch :default e
      (js/console.error "Unable to read EDN from local storage" e)
      nil)))

(defn set-edn [k v]
  (try
    (some->> v
             pr-str
             (js/localStorage.setItem k))
    (catch :default e
      (js/console.error "Unable to write EDN to local storage" e)
      nil)))
