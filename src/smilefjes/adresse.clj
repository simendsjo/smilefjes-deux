(ns smilefjes.adresse
  (:require [clojure.string :as str]))

(defn capitalize-word [s beginning-of-sentence?]
  (let [ss (str/lower-case s)]
    (cond
      (= "c/o" ss) "C/O"
      (= "CC" s) "CC"
      beginning-of-sentence? (str/capitalize s)
      (= "i" ss) "i"
      (= "på" ss) "på"
      (= "vei" ss) "vei"
      (= "veg" ss) "veg"
      (= "gate" ss) "gate"
      (re-find #"^\d" s) s
      :else (str/capitalize s))))

(defn index-of [haystack needle]
  (let [idx (.indexOf haystack needle)]
    (when (<= 0 idx)
      idx)))

(defn normalize-poststed [poststed]
  (loop [s (str/replace (str/trim poststed) #" +" " ")
         res ""]
    (if (not-empty s)
      (let [space (index-of s " ")
            dash (index-of s "-")
            parens (index-of s "(")
            beg? (= 0 (count res))]
        (cond
          (= parens 0)
          (recur (.substring s 1) (str res "("))

          (and space (or (nil? dash) (< space dash)))
          (recur (.substring s (inc space))
                 (str res (capitalize-word (.substring s 0 space) beg?) " "))

          dash
          (recur (.substring s (inc dash))
                 (str res (capitalize-word (.substring s 0 dash) beg?) "-"))

          :else
          (recur "" (str res (capitalize-word s beg?)))))
      res)))

(defn normalize-adresse [s]
  (normalize-poststed s))
