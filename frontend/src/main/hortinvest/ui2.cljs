(ns hortinvest.ui2
  (:require
   [hortinvest.ui2.menu :as menu]
   [hortinvest.ui2.util :as util]
   [syn-antd.col :refer [col]]
   [syn-antd.row :refer [row]]
   [syn-antd.layout :refer [layout layout-content]]))


(defn not-found-page []
  [:div
   [:h1 "404"]
   [:p "Not found"]])

(defn root-page [m]
  (util/redirect-to-default-page m))

(defn project-list-page [m]
  (util/redirect-to-default-page m))

(defn project-page
  [{:keys [data path-params]} _]
  (let [id (:id path-params)
        {:keys [src height]} (first (filter #(= id (:id %))
                                            (-> data :config :projects)))]
    [:div
     [row
      [col {:span 24}
       [:iframe {:allow "encrypted-media"
                 :frameBorder "0"
                 :height height
                 :onLoad #(js/setTimeout (fn []
                                           (.scroll js/window
                                                    (clj->js {:behaviour "smooth"
                                                              :left 0
                                                              :top 0})))
                                         1000)
                 :src src
                 :width "100%"}]]]]))

(defn results-page []
  [util/Redirect {:to :impact}])

(defn impact-page []
  [:div [:h2 "Impacts"]])

(defn outcome-list-page []
  [util/Redirect {:to :outcome
                  :path-params {:id 1}}])

(defn outcome-page [{:keys [path-params]}]
  (let [{:keys [id]} path-params]
    [:div [:h2 (str "Outcome " id)]]))

(defn content [app-state]
  [layout-content {:class "site-layout"
                   :style {:marginTop 140}}
   (let [match (:route-match @app-state)]
     (when match
       [(-> match :data :view) match app-state]))])

(defn root [app-state]
  [layout
   [menu/header app-state]
   [content app-state]])
