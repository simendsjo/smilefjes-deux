(ns smilefjes.pages
  (:require [smilefjes.pages.kommune-page :as kommune-page]
            [smilefjes.pages.map-page :as map-page]
            [smilefjes.pages.search-page :as search-page]
            [smilefjes.pages.spisested-page :as spisested-page]))

(defn render-page [ctx page]
  (case (:page/kind page)
    :page.kind/frontpage
    (search-page/render-page ctx page)

    :page.kind/spisested
    (spisested-page/render ctx page)

    :page.kind/kommune
    (kommune-page/render ctx page)

    :page.kind/spisested-index
    (search-page/render-index ctx)

    :page.kind/search-page
    (search-page/render-result-page ctx page)

    :page.kind/map-page
    (map-page/render-page ctx page)))
