(ns hortinvest.app
  (:require
   [hortinvest.impacts :refer [impacts]]
   [hortinvest.projects :refer [projects]]
   [reagent.core :as r]
   [reagent.dom :as rdom]
   [syn-antd.col :refer [col]]
   [syn-antd.menu :refer [menu menu-item]]
   [syn-antd.row :refer [row]]))


(defonce app-state (r/atom {:main-menu-selection "projects"}))

(defn main-menu-action [args]
  (let [{:strs [key]} (js->clj args)
        {:keys [main-menu-selection]} @app-state]
    (when (not (= key main-menu-selection))
      (swap! app-state assoc :main-menu-selection key))))

(defn content [app-state]
  (let [{:keys [main-menu-selection]} @app-state]
    (case main-menu-selection
      "impacts" [impacts]
      [projects])))

(defn main-menu [app-state]
  [menu {:mode "horizontal"
         :defaultSelectedKeys [(:main-menu-selection @app-state)]
         :onClick main-menu-action}
   [menu-item {:key "projects" :title "projects"} [:a "Projects"]]
   [menu-item {:key "impacts" :title "Impacts"} [:a "Impacts"]]])

(defn root []
  [:div {:class "container"
         :style {:margin-top "20px"}}
   [row {:style {:margin-bottom "20px"}}
    [col {:offset 1 :span 2} [:h1 "HortInvest"]]
    [col {:offset 1} [main-menu app-state]]]
   [:br]
   [content app-state]])

(defn init []
  (rdom/render [root] (js/document.getElementById "app")))
