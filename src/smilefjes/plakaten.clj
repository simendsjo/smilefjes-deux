(ns smilefjes.plakaten)

(defn oppsummer-smilefjeskarakter [smilefjeskarakter]
  (case smilefjeskarakter
    ("0" "1") "Tilsynet har ikke avdekket regelverksbrudd som krever oppfølging."
    ("2" "3") "Tilsynet har avdekket regelverksbrudd som krever oppfølging."))

(defn beskriv-karakter [smilefjeskarakter]
  (case smilefjeskarakter
    ("0" "1") "blidt smilefjes"
    "2" "strekmunn"
    "3" "sur munn"))

(defn smilefjes-svg-url [smilefjeskarakter]
  (str "/images/"
       (case smilefjeskarakter
         ("0" "1") "smilefjes"
         "2" "strekmunn"
         "3" "surmunn")
       ".svg"))
