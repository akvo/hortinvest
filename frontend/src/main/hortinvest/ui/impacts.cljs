(ns hortinvest.ui.impacts
  (:require
   [hortinvest.ui.impacts-data :as data]
   [syn-antd.col :refer [col]]
   [syn-antd.progress :refer [progress]]
   [syn-antd.row :refer [row]]))

(defn load-projects []
  (data/load))

(defn indicator-periods [i]
  (map-indexed
   (fn [item-id period]
     #_[:li {:name (str "period-" item-id) :key (str "period-" item-id)}
        "Actual value: " (:actual_value period) "  ... "
      " Target value: " (:target_value period)]
     [row {:gutter 20 :key (str "period-" item-id)}
     [col {:span 8} [:p (:indicator_unicode period)]]
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
                 :width 50}]]]

     )
   (:periods i)))

(defn impact-indicators [impact]
  (map-indexed
   (fn [item-id i]
     [:div {:key (str "indicator-div-" item-id)}
      [row {:gutter 20 :key (str "indicator-" item-id)}
       [col {:span 8}
        [:p (:title i)]]
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
      (indicator-periods i)
      ]
     #_[:li {:name (str "indicator-" item-id) :key (str "indicator-" item-id)}
      [:h4 {:class (project-type p)} (:title i)]
      #_(indicator-periods i)
      ])
   (:indicators impact)))

(defn show-impacts [impacts]
  (map-indexed
   (fn [item-id impact]
     [:div {:key (str (str "impact-div-" item-id))}
      [row
       [col {:span 24 :key (str "impact-" item-id)}
        [:h3 (:title impact)]]]
      (impact-indicators impact)
      (when (:outputs impact)
        (show-impacts (:outputs impact)))])
   impacts))

(defn impacts []
  [:div (show-impacts @data/db)])
