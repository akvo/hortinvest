(ns hortinvest.ui.results.outcomes
  (:require
   [syn-antd.col :refer [col]]
   [syn-antd.row :refer [row]]))


(defn outcomes []
  [row [col {:span 24}
        [:h3 "Outcomes"]]])
