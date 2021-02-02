(ns hortinvest.ui.impacts
  (:require
   [hortinvest.ui.impacts-data :as data]
   [clojure.string :refer [replace trim]]
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

(defn nan [x]
  (if (js/isNaN x) 0 x))

(defn to-int [x]
  (. js/Number parseInt x))

(defn period-value [v]
  (if (and v (not= v ""))
    (nan (to-int (trim (replace v #"\%" ""))))
    0))

(defn periods [r i]
  (let [periods (:periods i)]
    (reduce (fn [c p]
              (let [target (period-value (:target_value p))
                    actual (period-value (:actual_value p))
                    percent (nan (to-int (* (/ actual target) 100)))]
               (into c [[col {:span 4} [:div
                                        [:div (str (:period_start p) " - " (:period_end p))]
                                        [:div (str (period-value (:actual_value p)) " / " (period-value (:target_value p)))]
                                        [progress {:percent percent :size "small"}]]]])))
            r periods )))

(defn impact-indicators [impact]
  (map-indexed
   (fn [item-id i]
     [:div {:key (str "indicator-div-" item-id)}
      (periods [row {:gutter 20 :key (str "indicator-" item-id)}
                [col {:span 8} [:p (:title i)]]]
               i)])
   (:indicators impact)))

(defn impacts
  ([]
   [:div (impacts @data/db)])
  ([topics]
   (map-indexed
    (fn [item-id impact]
      [:div {:key (str (str "impact-div-" item-id))}
       [row
        [col {:span 24 :key (str "impact-" item-id)}
         [:h3 (:title impact)]]]
       (impact-indicators impact)
       (when (:outputs impact)
         (impacts (:outputs impact)))])
    topics)))
