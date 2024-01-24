(ns smilefjes.ui.storage)

(defn get-json [k]
  (try
    (some-> (js/localStorage.getItem k)
            js/JSON.parse)
    (catch :default e
      (js/console.error "Unable to read from local storage" e)
      nil)))

(defn get-edn [k]
  (some-> (get-json k) js->clj))

(defn set-json [k v]
  (try
    (some->> v
             js/JSON.stringify
             (js/localStorage.setItem k))
    (catch :default e
      (js/console.error "Unable to write to local storage" e)
      nil)))

(defn set-edn [k v]
  (some->> v clj->js (set-json k)))
