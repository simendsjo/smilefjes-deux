(ns smilefjes.tilsyn
  (:require [clojure.string :as str]))

(defn formatter-adresse [{:keys [linje1 linje2 poststed postnummer]}]
  (str/join ", " (filter not-empty [linje1 linje2 (str postnummer " " poststed)])))

(defn get-besøk [spisested]
  (->> (:tilsynsbesøk/_tilsynsobjekt spisested)
       (sort-by :tilsynsbesøk/dato)
       reverse))
