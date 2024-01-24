(ns smilefjes.ui.lokalavis
  (:require [smilefjes.ui.storage :as storage]))

(defn setup [kommunekode]
  (let [storage-key (str "kommune-" kommunekode)
        date-now (subs (pr-str (js/Date.)) 7 17)
        [date-prev-visit date-pprev-visit] (storage/get-edn storage-key)
        comparison-date (when date-prev-visit
                          (if (= date-now date-prev-visit)
                            date-pprev-visit
                            date-prev-visit))]
    (when (not= date-now date-prev-visit)
      (storage/set-edn storage-key [date-now date-prev-visit]))
    (when comparison-date
      (doseq [report (filter #(< comparison-date
                                 (.getAttribute % "data-last_visit_date"))
                             (js/document.querySelectorAll
                              "[data-last_visit_date]"))]
        (.add (.-classList report) "ny-rapport")))))
