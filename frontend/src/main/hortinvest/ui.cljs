(ns hortinvest.ui
  (:require
   [hortinvest.ui.impacts :as i]
   [hortinvest.ui.projects :refer [projects]]
   [hortinvest.util :as util]
   [syn-antd.col :refer [col]]
   [syn-antd.menu :refer [menu menu-item]]
   [syn-antd.row :refer [row]]))

(defn content [app-state dashboard-config]
  (let [{:keys [main-menu-selection]} @app-state]
    (case main-menu-selection
      "impact" (do
                  (i/load-projects)
                  [i/impacts])
      [projects app-state dashboard-config])))

(defn main-menu [app-state]
  [menu {:mode "horizontal"
         :defaultSelectedKeys [(:main-menu-selection @app-state)]
         :onClick #(util/menu-change app-state :main-menu-selection %)}
   [menu-item {:key "projects" :title "projects"} [:a "Projects"]]
   [menu-item {:key "impact" :title "Impacts"} [:a "Impact"]]])

(defn root [app-state dashboard-config]
  [:div {:class "container"
         :style {:margin-top "20px"}}
   [row {:style {:margin-bottom "20px"}}
    [col {:offset 1 :span 2} [:h1 "HortInvest"]]
    [col {:offset 1} [main-menu app-state]]]
   [:br]
   [content app-state dashboard-config]])
