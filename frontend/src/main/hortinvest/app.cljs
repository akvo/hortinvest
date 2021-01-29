(ns hortinvest.app
  (:require
   [hortinvest.ui :as ui]
   [reagent.core :as r]
   [reagent.dom :as rdom]))

(def dashboard-config
  [{:id "60117663-3feb-4b48-a44a-a02b6961a9bc"
    :title "Fruit tree & seed distribution"
    :height "4970"
    :src "https://hortinvest.akvolumen.org/s/2X0tZojm71g"}
   {:id "600eec0a-dd6e-46ae-a8d4-53b5fc3ad1a4"
    :title "Business cases"
    :height "6620"
    :src "https://hortinvest.akvolumen.org/s/534M4nFv6TE"}])

(defonce app-state
  (r/atom {:main-menu-selection "projects"
           :projects-menu-selection (-> dashboard-config first :id)}))

(defn init []
  (rdom/render [ui/root app-state dashboard-config]
               (js/document.getElementById "app")))
