(ns smilefjes.core-test
  (:require [clojure.test :refer [deftest is]]
            [smilefjes.core :as sut]))

(deftest app-config-test
  (is (= (:site/title (sut/create-app :dev)) "Smilefjes")))
