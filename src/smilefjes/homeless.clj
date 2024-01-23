(ns smilefjes.homeless
  (:require [superstring.core :as str])
  (:import (java.text Normalizer)))

(defn slugify [s]
  (-> (str/lower-case s)
      str/trim
      (Normalizer/normalize java.text.Normalizer$Form/NFD)
      (str/replace #"[\u0300-\u036F]" "")
      (str/replace #"[^a-z 0-9]" "")
      (str/replace #" +" "_")))
