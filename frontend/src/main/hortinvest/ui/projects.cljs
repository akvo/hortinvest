(ns hortinvest.ui.projects
  (:require
   [goog.string :as gstring]
   [goog.string.format]
   [syn-antd.col :refer [col]]
   [syn-antd.row :refer [row]]))


(defn iframe-html [{:keys [height src]}]
  (gstring/format "<iframe width='100%' height='%spx' src='%s' frameborder='0' allow='encrypted-media'></iframe>"
                  height
                  src))

(defn iframe [config]
  [:div {:dangerouslySetInnerHTML {:__html (iframe-html config)}}])

(defn projects [app-state]
  (let [{:keys [current-page projects]} @app-state
        dashboard (first (filter #(= (:id %) (first current-page))
                                 (:config projects)))]
    [row
     [col {:span 24}
      [iframe dashboard]]]))
