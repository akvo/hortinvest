(ns hortinvest.ui.impacts
  (:require
   [hortinvest.ui.impacts-data :as data]
   [clojure.string :refer [replace trim]]
   [syn-antd.col :refer [col]]
   [syn-antd.progress :refer [progress]]
   [syn-antd.row :refer [row]]
   [syn-antd.list :as slist]
   [syn-antd.card :refer  [card]]))

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
  (let [periods (:periods i)
        empty-cols (- 4 (count periods))]
    (let [res (reduce (fn [c p]
                        (let [target (period-value (:target_value p))
                              actual (period-value (:actual_value p))
                              percent (nan (to-int (* (/ actual target) 100)))]
                          (conj c [col {:span 4}
                                   [:div
                                      [:div {:style {:fontSize "11px"}}(str (:period_start p) " - " (:period_end p))]
                                      [:div (str (period-value (:actual_value p)) " / " (period-value (:target_value p)))]
                                      [progress {:percent percent :size "small"}]]])))
                      r periods )]
      (if (pos? empty-cols)
        (into res (vec (repeat empty-cols [col {:span 4} "."])))
        res))))


(defn impact-indicators [impact]
  (map-indexed
   (fn [item-id i]
     [slist/list-item {:key (str (str "indicator-div-" item-id)) :style {:width "100%"}}
      (into [row {:style {:width "100%"}}] (periods [[col {:span 8} (:title i)]] i))])
   (:indicators impact)))

(defn impacts
  ([]
   (when (not (empty? @data/db))
     (into [:div {:style {:margin "20px"}}]
           (impacts [] @data/db)))
   )
  ([container topics]
   (reduce
    (fn [c impact]
      (let [res [row {:span 24 :key (str (str "impact-div-" (:id impact))) :style {:margin "20px"}}
                 [card {:title (:title impact) :style {:width "90%"}}
                  [slist/list { #_:header #_(str (count (:indicators impact)) " indicators")}
                   (impact-indicators impact)]]]]
        (if (:outputs impact)
          (impacts (conj c res) (:outputs impact))
          (conj c res))

        ))

    container topics)))
