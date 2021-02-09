(ns hortinvest.ui.impacts
  (:require
   [clojure.string :refer [replace trim split upper-case]]
   [hortinvest.ui.impacts-data :as data]
   [hortinvest.util :as util]
   [cljs-time.format :refer (formatter parse unparse)]
   [reagent.core :as r]
   [goog.string :as gstring]
   [syn-antd.card :refer  [card]]
   [syn-antd.col :refer [col]]
   [syn-antd.list :as slist]
   [syn-antd.switch :as switch]
   [syn-antd.menu :refer [menu menu-item menu-sub-menu]]
   [syn-antd.progress :refer [progress]]
   [syn-antd.row :refer [row]]
   [syn-antd.typography :refer [typography-text typography-title]]))

(def data-date-formatter (formatter "yyyy-MM-dd"))
(def view-date-formatter (formatter "dd MMM yyyy"))

(defn format-date [s]
  (let [d (parse data-date-formatter s)]
    (upper-case (unparse view-date-formatter d))))

(defn dot
  [{:keys [showInfo percent] :or {showInfo true
                                  percent 0} :as opts}]
  [:<>
   [typography-text
    (merge (cond
             (>= percent 100) {:type "success" }
             (< percent 33) {:type "secondary"})
           opts)
    (gstring/unescapeEntities "&#9679;")]
   (when showInfo
     [typography-text {:type "secondary"
                       :style {:margin-left 8
                               :whiteSpace "nowrap"}}
      (str percent "%")])])

(def initial-menu-option ["3"])

(def view-db (r/atom {:switches {:percentages? true
                                 :disaggregated? false}
                      :menu {:option-selected initial-menu-option}}))

(defn menu-change [view-db event]
  (let [{:strs [key] :as a} (js->clj event)]
    (when (not= key (-> @view-db :menu :option-selected))
      (swap! view-db assoc-in [:menu :option-selected] key))))

(defn impacts-menu []
  [menu {:mode "horizontal"
         :defaultSelectedKeys initial-menu-option ;;(-> @app-state :current-page first)
         :onClick #(menu-change view-db %)}
   [menu-item {:key ["3"]} "Impact"]
   [menu-item {:key ["2" "1"]} "Outcome 1"]
   [menu-item {:key ["2" "2"]} "Outcome 2"]
   [menu-item {:key ["2" "3"]} "Outcome 3"]
   [menu-item {:key ["2" "4"]} "Outcome 4"]])


(defn load-projects []
  (data/load))

(defn period-value [v]
  (if (and v (not= v ""))
    (util/nan (util/to-int (trim (replace v #"\%" ""))))
    0))

(defn dates [r i]
  (let [periods (:periods i)
        empty-cols (- 4 (count periods))]
    (let [res (reduce (fn [c p]
                        (conj c [col {:span 4}
                                 [:div {:style { :width "100%"}}
                                  [:div {:style {:fontSize "11px" :textAlign "right" :whiteSpace "nowrap"}}
                                   (str (format-date (:period_start p)) " - " (format-date (:period_end p)))]]]))
                      r periods )]
      (if (pos? empty-cols)
        (into res (vec (repeat empty-cols [col {:span 4} "."])))
        res))))

(defn periods [r i switches]
  (let [periods (:periods i)
        empty-cols (- 4 (count periods))]
    (let [res (reduce (fn [c p]
                        (let [target (period-value (:target_value p))
                              actual (period-value (:actual_value p))
                              percent (util/nan (util/to-int (* (/ actual target) 100)))]
                          (conj c [col {:span 4}
                                   [:div {:style { :width "100%"}}
                                    #_[:div {:style {:fontSize "11px" :textAlign "right"}}
                                     (str (:period_start p) " - " (:period_end p))]
                                    [:div {:style {:textAlign "right" :width "50%" :float "left"}}
                                     (period-value (:actual_value p))
                                     [:br]
                                     (period-value (:target_value p))]
                                    [:div {:style {:width "50%" :float "right"
                                                   :padding "10px"
                                                   :paddingLeft "20px"}}
                                     (when (-> switches :percentages?) [dot {:percent percent :style {:whiteSpace "nowrap"}}])]

                                    ]])))
                      r periods )]
      (if (pos? empty-cols)
        (into res (vec (repeat empty-cols [col {:span 4} "."])))
        res))))

(defn impact-indicators [impact partners-data switches]
  (map-indexed
   (fn [item-id i]
     (let [res (into [slist/list {:key (str "impact-li" (:id impact) item-id)}]
                     [[slist/list-item {:key (str (str "indicator-div-" item-id)) :style {:width "100%"}}
                       (into [row {:style {:width "100%"}}]
                             (periods [[col {:span 8 :style {:padding-right "15px"}} (:title i)]] i switches))]])]
       res
       (if-let [pd (and (:disaggregated? switches) (seq (filter (fn [[k v]] (get v (data/partner-indicator-key impact i))) partners-data)))]
         (into res
               (reduce
                (fn [c partner]
                  (let [partner-title (:title (key partner))
                        partner-periods {:periods (get (val partner) (data/partner-indicator-key impact i))}]
                    (conj c [slist/list-item {:key (str (str "indicator-div-" item-id partner-title)) :style {:width "100%"}}
                             (into [row {:style {:width "100%"}}]
                                   (periods [[col
                                              {:span 8 :style {:padding-right "15px"}}
                                              (str partner-title )]]
                                            partner-periods switches))])))
                [[slist/list-item {:key (str (str "indicator-div-" item-id "-contributors")) :style {:width "100%"}}
                  [row {:style {:width "100%"}} [col  "Contributors"]]]] pd)
               )
         res)))
   (:indicators impact)))

(defn impacts
  ([]
   (when (and (not (empty? (get @data/db data/main-project)))
              (= 5 (count @data/partners)))
     [:div
      [:div {:class "ant-menu-horizontal"
             :style {:float "right"}}
       [:div {:style {:marginRight "10px"}}
        [:span  "Percentages"]
        [switch/switch
         {:checked (-> @view-db :switches :percentages?)
          :style {:marginRight "30px"
                  :marginLeft "10px"}
          :on-change #(swap! view-db update-in [:switches :percentages?] not (js->clj %))
          :size "small"}]
        [:span  "Contributors"]
        [switch/switch
         {:checked (-> @view-db :switches :disaggregated?)
          :style {:marginLeft "10px"}
          :on-change #(swap! view-db update-in [:switches :disaggregated?] not (js->clj %))
          :size "small"}]
        ]
       ]
      [row {:span 24}

       [col {:span 24}
        (impacts-menu)]
]
      (into [:div {:style {:margin "20px"}}]
            (let [option-selected (-> @view-db :menu :option-selected)
                  outcome-selected (second (split option-selected #","))]
              (impacts [] (filter #(and (= (first option-selected)  (:type %))
                                        (or (nil? outcome-selected)
                                            (= (util/to-int outcome-selected)
                                               (data/outcome-level (:title %)))))
                                  (get @data/db data/main-project)) @data/partners)))]))
  ([container topics partners-data]
   (reduce
    (fn [c impact]
      (let [res [row {:span 24 :key (str (str "impact-div-" (:id impact))) :style {:margin "20px"}}
                 (into [card {:title (:title impact) :style {:width "90%"}}]
                       (into (let [i (first (:indicators impact))]
                               [[slist/list {:key (str "impact-li-dates" (:id impact))}
                                 [slist/list-item {:key (str (str "indicator-div-date" (:id impact))) :style {:width "100%"}}
                                  (into [row {:style {:width "100%"}}]
                                        (dates [[col {:span 8 :style {:padding-right "15px"}} ]] i))
                                  ]]])
                              [(impact-indicators impact partners-data (:switches @view-db))]))]]
        (if (:outputs impact)
          (impacts (conj c res) (:outputs impact) partners-data)
          (conj c res))))
    container topics)))
