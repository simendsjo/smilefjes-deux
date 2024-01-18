(ns smilefjes.adresse-test
  (:require [clojure.test :refer [deftest is]]
            [smilefjes.adresse :as sut]))

(deftest normalize-poststed-test
  (is (= (sut/normalize-poststed "VÅLER I SOLØR")
         "Våler i Solør"))

  (is (= (sut/normalize-poststed "NORD-LENANGEN")
         "Nord-Lenangen"))

  (is (= (sut/normalize-poststed "Leirfjord")
         "Leirfjord"))

  (is (= (sut/normalize-poststed "KRISTIANSAND S")
         "Kristiansand S"))

  (is (= (sut/normalize-poststed "NES PÅ HEDMARKEN")
         "Nes på Hedmarken")))

(deftest normalize-adresse-test
  (is (= (sut/normalize-adresse " Hamarbispens vei 208") "Hamarbispens vei 208"))

  (is (= (sut/normalize-adresse "2. ETASJE BIBLIOTEKGATA 30")
         "2. Etasje Bibliotekgata 30"))

  (is (= (sut/normalize-adresse "AKERSHUSSTRANDA 13    Skur 34   Skur 34 ")
         "Akershusstranda 13 Skur 34 Skur 34"))

  (is (= (sut/normalize-adresse "ARTHUR ARNTZENS VEG 10")
         "Arthur Arntzens veg 10"))

  (is (= (sut/normalize-adresse "Bakkegata 4A")
         "Bakkegata 4A"))

  (is (= (sut/normalize-adresse "Ferjesambandet Bognes - Skarberget (A-rute) ")
         "Ferjesambandet Bognes - Skarberget (A-Rute)"))

  (is (= (sut/normalize-adresse "Ferjesambadet Stangnes - Sørrollnes")
         "Ferjesambadet Stangnes - Sørrollnes"))

  (is (= (sut/normalize-adresse "Gate 1, nr. 203, ")
         "Gate 1, Nr. 203,"))

  (is (= (sut/normalize-adresse "Skomakerstua Cafe, bred and breakfast Postboks 79 Postboks 79")
         "Skomakerstua Cafe, Bred And Breakfast Postboks 79 Postboks 79"))

  (is (= (sut/normalize-adresse "C/O TORGHALLEN KONGENS GATE 42")
         "C/O Torghallen Kongens gate 42"))

  (is (= (sut/normalize-adresse "CC GJØVIK JERNBANESVINGEN 6")
         "CC Gjøvik Jernbanesvingen 6"))

  (is (= (sut/normalize-adresse "FERGE HALSA-KANESTRAUM")
         "Ferge Halsa-Kanestraum")))
