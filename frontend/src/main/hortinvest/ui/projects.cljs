(ns hortinvest.ui.projects
  (:require
   [goog.string.format]
   [hortinvest.util :as util]
   [syn-antd.col :refer [col]]
   [syn-antd.row :refer [row]]))


(defn projects [app-state]
  (util/track-page-view "projects")
  (let [{:keys [current-page projects]} @app-state
        {:keys [height src]} (first (filter #(= (:id %) (first current-page))
                                            (:config projects)))]
    [row
     [col {:span 24}
      [:iframe {:allow "encrypted-media"
                :frameBorder "0"
                :height height
                :onLoad #(js/setTimeout (fn []
                                          (.scroll js/window
                                                   (clj->js {:behaviour "smooth"
                                                             :left 0
                                                             :top 0})))
                                        1000)
                :src src
                :width "100%"}]]]))
