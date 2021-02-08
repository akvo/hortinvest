(ns hortinvest.ui
  (:require
   [hortinvest.ui.projects :refer [projects]]
   [hortinvest.ui.results :refer [results]]
   [hortinvest.util :as util]
   [syn-antd.menu :refer [menu menu-item menu-sub-menu]]
   [syn-antd.layout :refer [layout layout-header layout-content]]))


(defn header-menu [app-state]
  [menu {:theme "dark"
         :mode "horizontal"
         :defaultSelectedKeys (-> @app-state :current-page first)
         :onClick #(util/menu-change app-state %)}
   (reduce (fn [menu {:keys [id title]}]
             (conj menu
                   [menu-item {:key id} title]))
           [menu-sub-menu {:key "projects" :title "Projects"}]
           (-> @app-state :projects :config))
   [menu-sub-menu {:key "results" :title "Results"}
    [menu-item {:key "impacts"} "Impacts"]
    [menu-item {:key "outcomes"} "Outcomes"]]])

(defn header [app-state]
  [layout-header
   {:style {:position "fixed"
            :zIndex 1
            :width "100%"}}
   [:div {:class "logo"} [:h1 "Hortinvest"]]
   [header-menu app-state]])

(defn content [app-state]
  [layout-content {:class "site-layout"
                   :style {:padding "0 50px"
                           :marginTop 64}}
   [:div {:class "site-layout-background"
          :style {:padding 24
                  :minHeight 380}}
    (case (-> @app-state :current-page last)
      "results" (results app-state)
      (projects app-state))]])

(defn root [app-state]
  [layout
   [header app-state]
   [content app-state]])
