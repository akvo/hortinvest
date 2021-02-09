(ns hortinvest.ui.impacts
  (:require
   [reagent.core :as r]
   [hortinvest.ui.impacts-data :as data]
   [clojure.string :refer [replace trim]]
   [syn-antd.col :refer [col]]
   [syn-antd.progress :refer [progress]]
   [syn-antd.row :refer [row]]
   [syn-antd.menu :refer [menu menu-item menu-sub-menu]]
   [syn-antd.list :as slist]
   [syn-antd.card :refer  [card]]))

(defn load-projects []
  (data/load))

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
                                      [:div {:style {:fontSize "11px"}} (str (:period_start p) " - " (:period_end p))]
                                      [:div (str (period-value (:actual_value p)) " / " (period-value (:target_value p)))]
                                      [progress {:percent percent :size "small" :style {:padding "8px"}}]]])))
                      r periods )]
      (if (pos? empty-cols)
        (into res (vec (repeat empty-cols [col {:span 4} "."])))
        res))))
(defn partner-indicator-key [impact indicator]
  (str (:title impact) "- - - - -" (:title indicator)))

(let [k2 "1. Increased sustainable production and supplies of horticultural produce from smallholders (M/F)- - - - -1.1. Number of farmholders (male/female; 50% women; 15% < 30 age) with increased productivity and/or income"]
  (filter (fn [[k v]] (get v k2)) @data/partners))

(defn impact-indicators [impact partners-data]
  (map-indexed
   (fn [item-id i]
     (let [res (into [slist/list {:key (str "impact-li" (:id impact) item-id)}]
                     [[slist/list-item {:key (str (str "indicator-div-" item-id)) :style {:width "100%"}}
                       (into [row {:style {:width "100%"}}]
                             (periods [[col {:span 8 :style {:padding-right "15px"}} (:title i)]] i))]
                      ])]
       res
       (if-let [pd (seq (filter (fn [[k v]] (get v (partner-indicator-key impact i))) partners-data))]
         (do
;;           (println (partner-indicator-key impact i))
           (into res
                (reduce
                 (fn [c partner]
                   (let [partner-title (:title (key partner))
                         partner-periods {:periods (get (val partner) (str (:title impact) "- - - - -" (:title i)))}]

                     (conj c [slist/list-item {:key (str (str "indicator-div-" item-id partner-title)) :style {:width "100%"}}
                              (into [row {:style {:width "100%"}}]
                                    (periods [[col
                                               {:span 8 :style {:padding-right "15px"}}
                                               (str partner-title )]]
                                             partner-periods)
                                    )]))
                   )
                 [[slist/list-item {:key (str (str "indicator-div-" item-id "-contributors")) :style {:width "100%"}}
                   [row {:style {:width "100%"}} [col  "Contributors"]]]] pd)
                ))
         res)))
   (:indicators impact)))

(def menu-option-selected (r/atom ["30847" "2"]))

(defn menu-change [app-state event]
  (let [{:strs [keyPath] :as a} (js->clj event)]
    (when (not= keyPath @menu-option-selected)
      (reset! menu-option-selected keyPath))))

(defn impacts
  ([]
   (when (and (not (empty? (get @data/db data/main-project)))
              (= 5 (count @data/partners)))
     [:div
      [menu {:mode "horizontal"
             :defaultSelectedKeys "2";;(-> @app-state :current-page first)
             :onClick #(menu-change menu-option-selected %)}

       (reduce (fn [menu {:keys [id title]}]
                 (conj menu
                       [menu-item {:key id} title]))
               [menu-sub-menu {:key "2" :title "Outcomes"}]
               (:outcomes @data/menu))
       (reduce (fn [menu {:keys [id title]}]
                 (conj menu
                       [menu-item {:key id} title]))
               [menu-sub-menu {:key "3" :title "Impact"}]
               (:impacts @data/menu))]
      (into [:div {:style {:margin "20px"}}]
            (impacts [] (filter #(= (first @menu-option-selected) (str (:id %)))
                                (get @data/db data/main-project)) @data/partners))]))
  ([container topics partners-data]
   (reduce
    (fn [c impact]
      (let [res [row {:span 24 :key (str (str "impact-div-" (:id impact))) :style {:margin "20px"}}
                 [card {:title (:title impact) :style {:width "90%"}}
                  (impact-indicators impact partners-data)]]]
        (if (:outputs impact)
          (impacts (conj c res) (:outputs impact) partners-data)
          (conj c res))))
    container topics)))
