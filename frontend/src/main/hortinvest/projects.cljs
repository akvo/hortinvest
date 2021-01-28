(ns hortinvest.projects
  (:require
   [syn-antd.col :refer [col]]
   [syn-antd.row :refer [row]]))


(defn projects []
  [row
   [col {:span 24}
    [:h4 "Projects"]
    [:p "..."]]])
