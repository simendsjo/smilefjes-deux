(ns smilefjes.ui.lokalavis
  (:require [smilefjes.ui.storage :as storage]))

(defn setup [kommunekode]
  (let [storage-key (str "kommune-" kommunekode)
        elements (js/document.querySelectorAll
                  "[data-last_visit_date]")
        newest-date (last (sort (map #(.getAttribute % "data-last_visit_date") elements)))
        [date-prev-visit date-pprev-visit] (storage/get-json-edn storage-key)
        comparison-date (when date-prev-visit
                          (if (= newest-date date-prev-visit)
                            date-pprev-visit
                            date-prev-visit))]
    (when (not= newest-date date-prev-visit)
      (storage/set-json-edn storage-key [newest-date date-prev-visit]))
    (when comparison-date
      (doseq [report (filter #(< comparison-date
                                 (.getAttribute % "data-last_visit_date"))
                             elements)]
        (.add (.-classList report) "ny-rapport")))))
