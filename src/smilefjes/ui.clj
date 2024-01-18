(ns smilefjes.ui)

(defn layout [ctx & body]
  [:html
   [:body
    body
    (when-let [site-id (:matomo/site-id ctx)]
      [:img {:data-src (str "https://mattilsynet.matomo.cloud/matomo.php?idsite="
                            site-id
                            "&rec=1"
                            "&url={url}"
                            "&action_name={title}"
                            "&ua={ua}"
                            "&urlref={referrer}")
             :id "smilefjes-tracking-pixel"
             :style "border:0"
             :alt ""}])]])
