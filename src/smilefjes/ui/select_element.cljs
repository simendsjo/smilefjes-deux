(ns smilefjes.ui.select-element)

(defn handle-clicks [e]
  (when-let [element (.closest (.-target e) "[data-select_element_id]")]

    ;; update selected class
    (when-let [selected-class (.getAttribute element "data-selected_class")]
      (let [parent (.closest element ".js-select-element-parent")]
        (doseq [sibling (.querySelectorAll parent (str "." selected-class))]
          (.remove (.-classList sibling) selected-class)))
      (.add (.-classList element) selected-class))

    ;; change which element is hidden
    (let [selectee (js/document.getElementById
                    (.getAttribute element "data-select_element_id"))]
      (doseq [sibling (.-childNodes (.-parentNode selectee))]
        (.add (.-classList sibling) "hidden"))
      (.remove (.-classList selectee) "hidden"))))
