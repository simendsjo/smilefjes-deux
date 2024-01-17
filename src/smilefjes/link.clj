(ns smilefjes.link)

(defn link-to [ctx page]
  (if (and (:page/link page) (-> ctx :powerpack/app :site/base-url))
    (:page/link page)
    (:page/uri page)))
