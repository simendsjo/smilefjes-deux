(ns smilefjes.ui.dom
  (:require [clojure.string :as str]))

(defn get-params []
  (when (seq js/location.search)
    (let [raw-tokens (str/split (subs js/location.search 1) #"[=&]")
          tokens (cond-> raw-tokens
                   (odd? (count raw-tokens))
                   butlast)]
      (update-vals (apply hash-map tokens)
                   #(str/replace (js/decodeURIComponent %) #"\+" " ")))))

(defn qsa
  ([selector]
   (seq (js/document.querySelectorAll selector)))
  ([el selector]
   (when el
     (seq (.querySelectorAll el selector)))))

(defn qs
  ([selector]
   (js/document.querySelector selector))
  ([el selector]
   (when el
     (.querySelector el selector))))

(defn remove-class [el class]
  (when el
    (.remove (.-classList el) class)))

(defn add-class [el class]
  (when el
    (.add (.-classList el) class)))

(def loader
  "<span class=\"relative flex h-3 w-3\">
     <span class=\"animate-ping absolute inline-flex h-full w-full rounded-full bg-sky-400 opacity-75\"></span>
     <span class=\"relative inline-flex rounded-full h-3 w-3 bg-sky-500\"></span>
   </span>")
