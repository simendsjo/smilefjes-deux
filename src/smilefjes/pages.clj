(ns smilefjes.pages
  (:require [smilefjes.pages.search-page :as search-page]
            [smilefjes.pages.spisested-page :as spisested-page]))

(defn render-page [ctx page]
  (case (:page/kind page)
    :page.kind/frontpage
    (search-page/render-page ctx page)

    :page.kind/spisested
    (spisested-page/render ctx page)

    :page.kind/spisested-index
    (search-page/render-index ctx)

    :page.kind/search-page
    (search-page/render-result-page ctx page)))
