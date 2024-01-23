(ns smilefjes.homeless-test
  (:require [clojure.test :refer [deftest is]]
            [smilefjes.homeless :as sut]))

(deftest slugify-test
  (is (= (sut/slugify " Aalan Gård, Gårdskafè")
         "aalan_gard_gardskafe"))

  (is (= (sut/slugify "O`Learys (SSP avd. 2406)")
         "olearys_ssp_avd_2406")))
