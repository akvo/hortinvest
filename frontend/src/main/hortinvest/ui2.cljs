(ns hortinvest.ui2
  (:require
   [syn-antd.layout :refer [layout layout-content]]))


(defn not-found-page []
  [:div
   [:h1 "404"]
   [:p "Not found"]])

(defn root-page []
  [:div [:h1 "Hortinvest"]])

(defn content [app-state]
  [layout-content {:class "site-layout"
                   :style {:marginTop 140}}
   (let [match (:route-match @app-state)]
     (when match
       [(-> match :data :view) match app-state]))])

(defn root [app-state]
  [layout
   [content app-state]])
