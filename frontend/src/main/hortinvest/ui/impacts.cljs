(ns hortinvest.ui.impacts
  (:require
   [clojure.string :refer [replace trim split upper-case]]
   [cljs.pprint :refer [cl-format]]
   [hortinvest.ui.impacts-data :as data]
   [hortinvest.util :as util :refer [grid-opts]]
   [cljs-time.format :refer (formatter parse unparse)]
   [reagent.core :as r]
   [goog.string :as gstring]
   [syn-antd.card :refer  [card]]
   [syn-antd.result :refer  [result]]
   [syn-antd.col :refer [col]]
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

(defn int-comma
  "http://clojurescriptmadeeasy.com/blog/how-to-humanize-text-cl-format.html"
  [n] (cl-format nil "~:d" n))

(defn period-value [v]
  (if (and v (not= v ""))
    (util/nan (util/to-int (trim (replace v #"\%" ""))))
    0))

(defn dates [r i]
  (let [periods (:periods i)
        empty-cols (- 4 (count periods))]
    (let [res (reduce (fn [c p]
                        (conj c [col (grid-opts {:span 4} {:fontSize "11px" :textAlign "center" :whiteSpace "nowrap"} "black")
                                 (str (format-date (:period_start p)) " - " (format-date (:period_end p)))]))
                      r periods )]
      (if (pos? empty-cols)
        (into res (vec (repeat empty-cols [col (grid-opts {:span 4}) ""])))
        res))))

(defn periods [r i switches & [contributors?]]
  (let [periods (:periods i)
        empty-cols (- 4 (count periods))]
    (let [res (reduce (fn [c p]
                        (let [target (period-value (:target_value p))
                              actual (period-value (:actual_value p))
                              percent (util/nan (util/to-int (* (/ actual target) 100)))]
                          (conj c [col (grid-opts {:span 4}  (when contributors?
                                                               {:border-bottom "1px solid #EEE"
                                                                :margin-bottom "10px"}))
                                   (if contributors?
                                     [row (grid-opts {}  {:font-size "11px"
                                                          :margin-top "10px"}
                                                     "green")
                                      [col (grid-opts {:span 12} {:textAlign "right"} "red")
                                       (int-comma actual)]
                                      [col (grid-opts {:span 12})]]
                                     [row (grid-opts {}  (merge
                                                          {:font-size "11px"
                                                           :margin-top "10px"}
                                                          (when contributors?
                                                            {:border-bottom "1px solid #EEE"
                                                             :margin-bottom "10px"})) "green")
                                      [col (grid-opts {:span 12} {:textAlign "right"} "red")
                                       (int-comma actual)
                                       [:br]
                                       (int-comma target)]
                                      [col (grid-opts {:span 1})]
                                      [col (grid-opts {:span 11} {:padding "10px"} "blue")
                                       (when (-> switches :percentages?) [dot {:percent percent :style {:whiteSpace "nowrap"}}])]


                                      ])])))
                      r periods )]
      (if (pos? empty-cols)
        (into res (vec (repeat empty-cols [col (grid-opts {:span 4} (merge
                                                                     {}
                                                                     (when contributors?
                                                                       {:border-bottom "1px solid #EEE"
                                                                        :margin-bottom "10px"}))) ""])))
        res))))

(defn impact-indicators [impact partners-data switches]
  (map-indexed
   (fn [item-id i]
     (let [res (into [row (grid-opts {:key (str "impact-li" (:id impact) item-id)} {:margin-bottom "20px"} "red")]
                     (periods [[col (grid-opts {:span 8} {:padding-right "15px"}) (:title i)]] i switches))]
       (if-let [pd (and (:disaggregated? switches) (seq (filter (fn [[k v]]
                                                                  (->>
                                                                   (get v (data/partner-indicator-key impact i))
                                                                   ;; TODO enable if we want to hide contributors with 0 as actual_value
                                                                   ;;(filter #(pos? (period-value (:actual_value %))))
                                                                   ;;seq
                                                                   )) partners-data)))]
         (into res
               [[row (grid-opts {} {:width "100%"})
                 (into [col (grid-opts {:span 24 })]
                       (reduce
                        (fn [c partner]
                          (let [partner-title (:title (key partner))
                                partner-periods {:periods (get (val partner) (data/partner-indicator-key impact i))}]
                            (conj c (into [row (grid-opts {} {:width "100%"} "green")]
                                          (periods [[col (grid-opts {:span 1})]
                                                    [col
                                                     (grid-opts {:span 7}  {
                                                                            :font-size "13px"
                                                                            :border-bottom "1px solid #EEE"
                                                                            :margin-top "10px"
                                                                            :margin-bottom "10px"
                                                                           ;; :padding-right "15px"
                                                                            })
                                                     partner-title]
                                                    ]

                                                   partner-periods switches true)))))

                        [] pd))]]
               )
         res)))
   (:indicators impact)))


(defn find-indicator-with-more-periods [indicators]
  (reduce (fn [i1 i2]
            (if (>= (count (:periods i1)) (count (:periods i2)))
              i1
              i2)) (first indicators) (next indicators)))

(defn api-error []
  [:div
   (into
    [result {:status "warning" :title "There are some problems trying to load the RSR data"}
     "An error has been reported and we'll fix it as soon as possible "
     [:hr]
     [:h4 "Technical error details reported:"]]
    (mapv #(vector :h5 (str (select-keys (last %) [:status :body :trace-redirects] ))) @data/api-error))])

(defn switch-menu []
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
    ]])

(defn impacts
  ([]
   (util/track-page-view "results")
   (if (seq @data/api-error)
     (api-error)
     (when (and (not (empty? (get @data/db data/main-project)))
                (= 5 (count @data/partners)))
       [:div
        (switch-menu)
        [row (grid-opts {:span 24} {} "cyan")
         [col (grid-opts {:span 24} {} "yellow")
          (impacts-menu)]]
        [row
         (into [col (grid-opts {:span 24 :margin "20px"} {} "red")]
               (let [option-selected (-> @view-db :menu :option-selected)
                     outcome-selected (second (split option-selected #","))]
                 (impacts []
                          (filter #(and (= (first option-selected)  (:type %))
                                        (or (nil? outcome-selected)
                                            (= (util/to-int outcome-selected)
                                               (data/outcome-level (:title %)))))
                                  (get @data/db data/main-project))
                          @data/partners
                          (:switches @view-db))))]])))
  ([m app-state] ;; ui2
   (util/track-page-view "results")
   (if (seq @data/api-error)
     (api-error)
     (when (and (not (empty? (get @data/db data/main-project)))
                (= 5 (count @data/partners)))
       [:div
        [row
         (into [col (grid-opts {:span 24 :margin "20px"} {} "red")]
               (impacts []
                        (filter #(= "3" (:type %))
                                (get @data/db data/main-project))
                        @data/partners
                        (:switches @app-state)))]])))

  ([container topics partners-data switches]
   (reduce
    (fn [c impact]
      (let [res [[row (grid-opts {:key (str (str "impact-div-1-" (:id impact))) } {:margin "20px"} "orange")
                  [col (grid-opts {:span 24 :key (str (str "impact-div-1-" (:id impact))) } {} "orange")
                   [:h3 (:title impact)]]
                  ]
                 [row (grid-opts {:span 24 :key (str (str "impact-div-" (:id impact))) } {:margin "20px"} "black")
                  (into [col (grid-opts {:span 24 :key (str (str "impact-col-" (:id impact))) } {} "black")]
                        (into (let [i (find-indicator-with-more-periods (:indicators impact))]
                                [(into [row (grid-opts {} {:width "100%" :margin-bottom "20px"} "orange")]
                                       (dates [[col (grid-opts {:span 8} {:padding-right "15px"}) ]] i))
                                 ])
                              [(impact-indicators impact partners-data switches)]))]]]
        (if (:outputs impact)
          (impacts (apply conj c res) (:outputs impact) partners-data switches)
          (apply conj c res))))
    container topics)))

(defn outcomes [{:keys [path-params]} app-state]
  (util/track-page-view "results")
  (if (seq @data/api-error)
    (api-error)
    (when (and (not (empty? (get @data/db data/main-project)))
               (= 5 (count @data/partners)))
      [:div
       [row
        (into [col (grid-opts {:span 24 :margin "20px"} {} "red")]
              (let [;; option-selected (-> @view-db :menu :option-selected)
                    option-selected (str  "2," (:id path-params))
                    outcome-selected (second (split option-selected #","))]
                (impacts []
                         (filter #(and (= (first option-selected)  (:type %))
                                       (or (nil? outcome-selected)
                                           (= (util/to-int outcome-selected)
                                              (data/outcome-level (:title %)))))
                                 (get @data/db data/main-project))
                         @data/partners
                         (:switches @app-state))))]])
    )
  )
