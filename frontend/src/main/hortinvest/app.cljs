(ns hortinvest.app
  (:require
   [hortinvest.impacts :refer [impacts]]
   [hortinvest.projects :refer [projects]]
   [reagent.core :as r]
   [reagent.dom :as rdom]
   [syn-antd.col :refer [col]]
   [syn-antd.menu :refer [menu menu-item]]
   [syn-antd.row :refer [row]]))

(def dashboard-config [{:id "60117663-3feb-4b48-a44a-a02b6961a9bc"
                        :title "Fruit tree & seed distribution"
                        :height "4970"
                        :src "https://hortinvest.akvolumen.org/s/2X0tZojm71g"}
                       {:id "600eec0a-dd6e-46ae-a8d4-53b5fc3ad1a4"
                        :title "Business cases"
                        :height "6620"
                        :src "https://hortinvest.akvolumen.org/s/534M4nFv6TE"}])

(defonce app-state (r/atom {:main-menu-selection "projects"
                            :projects-menu-selection (-> dashboard-config first :id)}))

(defn main-menu-action [args]
  (let [{:strs [key]} (js->clj args)
        {:keys [main-menu-selection]} @app-state]
    (when (not (= key main-menu-selection))
      (swap! app-state assoc :main-menu-selection key))))

(defn content [app-state]
  (let [{:keys [main-menu-selection]} @app-state]
    (case main-menu-selection
      "impacts" [impacts]
      [projects app-state dashboard-config])))

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
