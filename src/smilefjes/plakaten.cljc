(ns smilefjes.plakaten)

(defn oppsummer-smilefjeskarakter [smilefjeskarakter]
  (case smilefjeskarakter
    ("0" "1") "Mattilsynet har ikke avdekket regelverksbrudd som krever oppfølging."
    "2" "Mattilsynet har avdekket regelverksbrudd som krever oppfølging."
    "3" "Mattilsynet har avdekket alvorlig regelverksbrudd."))

(defn beskriv-karakter [smilefjeskarakter]
  (case smilefjeskarakter
    ("0" "1") "blidt smilefjes"
    "2" "strekmunn"
    "3" "sur munn"))
