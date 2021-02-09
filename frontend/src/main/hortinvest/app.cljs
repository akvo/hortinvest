(ns hortinvest.app
  (:require
   [hortinvest.config :as config]
   [hortinvest.ui :as ui]
   [reagent.core :as r]
   [reagent.dom :as rdom]))


(defonce app-state
  (r/atom {:current-page ["results"]
           :projects {:config config/dashboard-config}}))

(defn init []
  (rdom/render [ui/root app-state]
               (js/document.getElementById "app")))
