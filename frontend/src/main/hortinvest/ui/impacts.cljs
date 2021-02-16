(ns hortinvest.ui.impacts
  (:require
   [clojure.string :refer [replace trim split upper-case]]
   [cljs.pprint :refer [cl-format]]
   [hortinvest.ui.impacts-data :as data]
   [hortinvest.config :as config]
   [hortinvest.util :as util :refer [grid-opts]]
   [cljs-time.format :refer (formatter parse unparse)]
   [goog.string :as gstring]
   [syn-antd.result :refer  [result]]
   [syn-antd.col :refer [col]]
   [syn-antd.row :refer [row]]
   [syn-antd.typography :refer [typography-text]]))

(def data-date-formatter (formatter "yyyy-MM-dd"))
(def view-date-formatter (formatter "dd MMM yyyy"))

(defn load-projects []
  (data/load))

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

(defn int-comma
  "http://clojurescriptmadeeasy.com/blog/how-to-humanize-text-cl-format.html"
  [n] (cl-format nil "~:d" n))

(defn period-value [v]
  (if (and v (not= v ""))
    (util/nan (util/to-int (trim (replace v #"\%" ""))))
    0))

(defn dates [r i]
  (let [periods (:periods i)
        empty-cols (- 4 (count periods))
        res (reduce (fn [c p]
                      (conj c [col (grid-opts {:span 4} {:fontSize "11px" :textAlign "center" :whiteSpace "nowrap"} "black")
                               (str (format-date (:period_start p)) " - " (format-date (:period_end p)))]))
                    r periods )]
    (if (pos? empty-cols)
      (into res (vec (repeat empty-cols [col (grid-opts {:span 4}) ""])))
      res)))

(defn periods [r i switches & [contributors?]]
  (let [periods (:periods i)
        empty-cols (- 4 (count periods))
        res (reduce (fn [c p]
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
      res)))

(defn impact-indicators [impact partners-data switches]
  (map-indexed
   (fn [item-id i]
     (let [res (into [row (grid-opts {:key (str "impact-li" (:id impact) item-id)} {:margin-bottom "20px"} "red")]
                     (periods [[col (grid-opts {:span 8} {:padding-right "15px"}) (:title i)]] i switches))]
       (if-let [pd (and (:disaggregated? switches) (seq (filter (fn [[_ v]]
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


(defn impacts
  ([_ app-state]
   (util/track-page-view "results")
   (if (seq @data/api-error)
     (api-error)
     (when (data/loaded?)
       [:div {:style {:padding "20px 0"}}
        [row
         (into [col (grid-opts {:span 24 :margin "20px"} {} "red")]
               (impacts []
                        (filter #(= "3" (:type %))
                                (get @data/db config/main-project))
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
    (when (and (seq (get @data/db config/main-project))
               (= 5 (count @data/partners)))
      [:div {:style {:padding "20px 0"}}
       [row
        (into [col (grid-opts {:span 24 :margin "20px"} {} "red")]
              (let [option-selected (str  "2," (:id path-params))
                    outcome-selected (second (split option-selected #","))]
                (impacts []
                         (filter #(and (= (first option-selected)  (:type %))
                                       (or (nil? outcome-selected)
                                           (= (util/to-int outcome-selected)
                                              (data/outcome-level (:title %)))))
                                 (get @data/db config/main-project))
                         @data/partners
                         (:switches @app-state))))]])))
