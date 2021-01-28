(ns hortinvest.reports
  (:require
   [syn-antd.col :refer [col]]
   [syn-antd.row :refer [row]]))


(defn reports []
  [row
   [col {:span 6}
    [:h2 "Reports"]]])
