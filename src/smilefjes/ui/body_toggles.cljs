(ns smilefjes.ui.body-toggles)

(defn ready-for-change? [element]
  (let [now (.getTime (js/Date.))
        ready? (if-let [then (some-> (.getAttribute element "data-throttle_ms")
                                     parse-long)]
                 (< 200 (- now then))
                 true)]
    (when ready?
      (.setAttribute element "data-throttle_ms" now))
    ready?))

(defn handle-clicks [e]
  (when-let [element (.closest (.-target e) "[data-toggle_body_class]")]
    (when (ready-for-change? element)
      (let [class (.getAttribute element "data-toggle_body_class")]
        (.toggle (.-classList js/document.body) class)))))
