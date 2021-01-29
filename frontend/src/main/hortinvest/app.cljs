(ns hortinvest.app
  (:require
   [hortinvest.impacts :refer [impacts]]
   [hortinvest.projects :refer [projects]]
   [reagent.core :as r]
   [reagent.dom :as rdom]
   [syn-antd.col :refer [col]]
   [syn-antd.menu :refer [menu menu-item]]
   [syn-antd.row :refer [row]]))


(defonce page-state (r/atom {:selected-key "projects"}))

(defn on-menu-change [args]
  (let [{:strs [key]} (js->clj args)
        {:keys [selected-key]} @page-state]
    (when (not (= key selected-key))
      (swap! page-state assoc :selected-key key))))

(defn content [{:keys [selected-key]}]
  (case selected-key
    "impacts" [impacts]
    ;; "reports" [reports]
    [projects]))

(defn main-menu []
  [menu {:mode "horizontal"
         :defaultSelectedKeys [(:selected-key @page-state)]
         :onClick on-menu-change}
   [menu-item {:key "projects" :title "projects"} [:a "Projects"]]
   [menu-item {:key "impacts" :title "Impacts"} [:a "Impacts"]]])

(defn root []
  [:div {:class "container"
         :style {:margin-top "20px"}}
   [row {:style {:margin-bottom "20px"}}
    [col {:offset 1 :span 2} [:h1 "HortInvest"]]
    [col {:offset 1} [main-menu]]]
   [:br]
   [content @page-state]])

(defn init []
  (rdom/render [root] (js/document.getElementById "app")))
