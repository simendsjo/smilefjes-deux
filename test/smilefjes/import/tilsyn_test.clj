(ns smilefjes.import.tilsyn-test
  (:require [clojure.test :refer [deftest is]]
            [smilefjes.import.tilsyn :as sut]))

(deftest link-test
  (is (= (sut/get-tilsynsobjekt-link
          {:tilsynsobjektid "Z1703151057439673252OPMTI_Tilsynsobjekt"
           :poststed "Oslo"
           :navn "O`Learys (SSP avd. 2406)"})
         "/spisested/oslo/olearys_ssp_avd_2406.Z1703151057439673252OPMTI/"))

  (is (= (sut/get-tilsynsobjekt-link
          {:tilsynsobjektid "Z1006080424270381127GXAJR_Tilsynsobjekt"
           :poststed "Gamle Fredrikstad"
           :navn "Mormors Cafe"})
         "/spisested/gamle_fredrikstad/mormors_cafe.Z1006080424270381127GXAJR/")))

(deftest uri-test
  (is (= (sut/get-tilsynsobjekt-uri
          {:tilsynsobjektid "Z1703151057439673252OPMTI_Tilsynsobjekt"
           :poststed "Oslo"
           :navn "O`Learys (SSP avd. 2406)"})
         "/spisested/Z1703151057439673252OPMTI/")))
