(ns hortinvest.ui.results.impacts
  (:require
   [reagent.core :as r]
   [syn-antd.col :refer [col]]
   [syn-antd.progress :refer [progress]]
   [syn-antd.row :refer [row]]
   [syn-antd.typography :refer [typography-text typography-title]]))


(def impacts-data
  {:periods [{:id 1
              :start "01 Jan 2018"
              :end "31 Dec 2018"}
             {:id 1
              :start "01 Jan 2019"
              :end "31 Dec 2019"}
             {:id 1
              :start "01 Jan 2020"
              :end "31 Dec 2020"}
             {:id 1
              :start "01 Jan 2021"
              :end "31 Dec 2021"}]

   :impacts [{:title "The horticultural sectors relative contribution to the regional economy in NW Rwanda will have increased significantly.."
              :indicators [{:title "1.1 Increased accrued incomes from ..."
                            :periods [{:id 1
                                       :target_value 100
                                       :actual_value 0}
                                      {:id 2
                                       :target_value 100
                                       :actual_value 20}
                                      {:id 3
                                       :target_value 100
                                       :actual_value 75}
                                      {:id 4
                                       :target_value 100
                                       :actual_value 100}]}
                           {:title "1.2 Number of farmholder (male/female; age <30) that doubled their income"
                            :periods [{:id 1
                                       :target_value 1155777
                                       :actual_value 0}
                                      {:id 2
                                       :target_value 1155777
                                       :actual_value 20}
                                      {:id 3
                                       :target_value  1155777
                                       :actual_value 75}
                                      {:id 4
                                       :target_value  1155777
                                       :actual_value 100}]}]}
             {:title "Improved food and nutrition security of poor households"
              :indicators [{:title "2.1 Increased average daily consumption level of fruits and vegetables in Rwanda increased from the current level in producing households"
                            :periods [{:id 1
                                       :target_value 150
                                       :actual_value 0}
                                      {:id 2
                                       :target_value 150
                                       :actual_value 20}
                                      {:id 3
                                       :target_value 150
                                       :actual_value 75}
                                      {:id 4
                                       :target_value 150
                                       :actual_value 100}]}]}]})

(defn periods-header [periods]
  (reduce (fn [row {:keys [end start]}]
            (conj row
                  [col {:span 3}
                   [typography-text {:type "secondary"} (str start " - " end)]]))
          [row {:style {:margin-bottom 20}} [col {:span 12}]]
          periods))

(defn period-ui [{:keys [actual_value target_value]}]
  [:<>
   [col {:span 1}
    [typography-text actual_value]
    [:br]
    [typography-text target_value]]
   [col {:span 1}
    [progress {:type "circle"
               :percent 50
               :width 20
               :showInfo false}]
    ]
   [col {:span 1}]
   ])

;; (defn indicator-ui [{:keys [title periods]}]
;;   (reduce (fn [m p]
;;             (conj m (period-ui p))
;;             )
;;           [row [col {:span 12}]]
;;           periods))

(defn indicator-ui [{:keys [title periods]}]
  [row {:style {:margin-bottom 20} } [col {:span 12} title]
   (for [period periods]
     (period-ui period))])

(defn impact-ui [{:keys [indicators title]}]
  [:<>
   [row [col {:span 24} [typography-title {:level 3} title]]]
   (periods-header (:periods impacts-data))
   (reduce (fn [indicators indicator]
             (conj indicators
                   (indicator-ui indicator)))
           [:<>]
           indicators)])

(defn impacts []
  (reduce (fn [impacts impact]
            (conj impacts (impact-ui impact)))
          [:<>]
          (:impacts impacts-data)))
