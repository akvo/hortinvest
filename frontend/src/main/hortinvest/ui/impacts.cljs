(ns hortinvest.ui.impacts
  (:require
   [syn-antd.col :refer [col]]
   [syn-antd.progress :refer [progress]]
   [syn-antd.row :refer [row]]))


(defn impacts []
  [:div
   [row
    [col {:span 24}
     [:h3 "1. The horticultural sector’s relative contribution to the regional economy in NW Rwanda will have increased significantly"]]]
   [row {:gutter 20}
    [col {:span 8} [:h3 "Impacts"]]
    [col {:span 4} [:h3 "P1"]]
    [col {:span 4} [:h3 "p2"]]
    [col {:span 4} [:h3 "p3"]]
    [col {:span 4} [:h3 "p4"]]]
   [row {:gutter 20}
    [col {:span 8}
     [:p "Increased accrued incomes from horticulture (producers, other SMEs active in the value chain and exporters), gross income"]]
    [col {:span 4}
     [progress {:percent 0
                :size "small"}]]
    [col {:span 4}
     [progress {:percent 30
                :size "small"}]]
    [col {:span 4}
     [progress {:percent 70
                :size "small"}]]
    [col {:span 4}
     [progress {:percent 100
                :size "small"}]]]
   [row {:gutter 20}
    [col {:span 8} [:p "Number of farmholders (male/female; age < 30) that doubled their income"]]
    [col {:span 4}
     [progress {:percent 0
                :type "circle"
                :width 50}]]
    [col {:span 4}
     [progress {:percent 30
                :type "circle"
                :width 50}]]
    [col {:span 4}
     [progress {:percent 70
                :type "circle"
                :width 50}]]
    [col {:span 4}
     [progress {:percent 100
                :type "circle"
                :width 50}]]]])
