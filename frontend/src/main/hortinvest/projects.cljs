(ns hortinvest.projects
  (:require
   [goog.string :as gstring]
   [goog.string.format]
   [syn-antd.col :refer [col]]
   [syn-antd.row :refer [row]]))

(def dashboard-config
  {:id "60117663-3feb-4b48-a44a-a02b6961a9bc"
   :height "4970"
   :src "https://hortinvest.akvolumen.org/s/2X0tZojm71g"})

(defn iframe-html [{:keys [height src]}]
  (gstring/format "<iframe width='100%' height='%spx' src='%s' frameborder='0' allow='encrypted-media'></iframe>"
                  height
                  src))

(defn iframe [config]
  [:div {:dangerouslySetInnerHTML {:__html (iframe-html config)}}])

(defn projects []
  [row
   [col {:span 24}
    [:h4 "Projects"]
    [iframe dashboard-config]]])


(comment

  ;; [:div {:dangerouslySetInnerHTML {:__html "<iframe width='100%' height='4970px' src='https://hortinvest.akvolumen.org/s/2X0tZojm71g' frameborder='0' allow='encrypted-media'></iframe>"}}]
  ;; <iframe width="100%" height="1000px" src="https://hortinvest.akvolumen.org/s/2X0tZojm71g" frameborder="0" allow="encrypted-media"></iframe>

  )
