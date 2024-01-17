(ns smilefjes.import.tilsyn-test
  (:require [clojure.test :refer [deftest is]]
            [smilefjes.import.tilsyn :as sut]))

(deftest slugify-test
  (is (= (sut/slugify " Aalan Gård, Gårdskafè")
         "aalan_gard_gardskafe"))

  (is (= (sut/slugify "O`Learys (SSP avd. 2406)")
         "olearys_ssp_avd_2406")))

(deftest link-test
  (is (= (sut/get-tilsynsobjekt-link
          {:tilsynsobjektid "Z1703151057439673252OPMTI_Tilsynsobjekt"
           :poststed "Oslo"
           :navn "O`Learys (SSP avd. 2406)"})
         "/smilefjes/spisested/oslo/olearys_ssp_avd_2406.Z1703151057439673252OPMTI_Tilsynsobjekt/"))

  (is (= (sut/get-tilsynsobjekt-link
          {:tilsynsobjektid "Z1006080424270381127GXAJR_Tilsynsobjekt"
           :poststed "Gamle Fredrikstad"
           :navn "Mormors Cafe"})
         "/smilefjes/spisested/gamle_fredrikstad/mormors_cafe.Z1006080424270381127GXAJR_Tilsynsobjekt/")))

(deftest uri-test
  (is (= (sut/get-tilsynsobjekt-uri
          {:tilsynsobjektid "Z1703151057439673252OPMTI_Tilsynsobjekt"
           :poststed "Oslo"
           :navn "O`Learys (SSP avd. 2406)"})
         "/smilefjes/spisested/Z1703151057439673252OPMTI/")))
