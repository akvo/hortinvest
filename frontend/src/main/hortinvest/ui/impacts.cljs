(ns hortinvest.ui.impacts
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
   [reagent.core :as r]
   [reagent.dom :as rdom]
   [cljs-http.client :as http]
   [cljs.core.async :refer [<!]]
   [syn-antd.col :refer [col]]
   [syn-antd.progress :refer [progress]]
   [syn-antd.row :refer [row]]))

(def rsr-data (r/atom []))

(defn load-projects []
  (go (let [response (<! (http/get "./data.json"
                                   {:with-credentials? false}))]
        (swap! rsr-data (fn [_] (:body response))))))

(defn project-type [p]
  (condp = (:type p)
    "3" "impact"
    "2" "outcome"
    "1" "output"))

(defn indicator-periods [i]
  (map-indexed
   (fn [item-id period]
     #_[:li {:name (str "period-" item-id) :key (str "period-" item-id)}
        "Actual value: " (:actual_value period) "  ... "
      " Target value: " (:target_value period)]
     [row {:gutter 20 :key (str "period-" item-id)}
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
                 :width 50}]]]

     )
   (:periods i)))

(defn project-indicators [p]
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
   (:indicators p)))

(defn projects [projects-col]
  (map-indexed
   (fn [item-id project]
     [:div {:key (str (str "project-div-" item-id))}
      [row
       [col {:span 24 :key (str "project-" item-id)}
        [:h3 (:title project)]]]
      (project-indicators project)
      (when (:outputs project)
        (projects (:outputs project)))
      ])
   projects-col))

(defn impacts []
  [:div
   (projects @rsr-data)])
