(ns hortinvest.app
  (:require
   [clojure.string :refer [includes?]]
   ["@sentry/browser" :as Sentry]
   ["@sentry/tracing" :as tracing :refer [Integrations]]
   [hortinvest.config :as config]
   [hortinvest.util :as util]
   [hortinvest.ui :as ui]
   [hortinvest.ui2 :as ui2]
   [hortinvest.routes :as routes]
   [reagent.core :as r]
   [reagent.dom :as rdom]
   [reitit.frontend :as rf]
   [reitit.frontend.easy :as rfe]))


(def version 1)
;; (def version 2)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Version 1
;;

(defonce app-state
  (r/atom {:current-page ["results"]
           :projects {:config config/dashboard-config}}))

(defn init-version-1 []
  (rdom/render [ui/root app-state]
               (js/document.getElementById "app")))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Version 2
;;

(defonce app-state2
  (r/atom {:config config/config
           :route-match nil
           :switches {:percentages? true
                      :disaggregated? false}}))

(defn init-version-2 []
  (rfe/start!
   (rf/router routes/routes {:conflicts nil
                             :data {:config config/config}})
   (fn [m]
     (swap! app-state2 #(assoc % :route-match m)))
   {:use-fragment true})
  (rdom/render [ui2/root app-state2]
               (.getElementById js/document "app")))

(defn init []
  (when-not (includes? util/host "localhost")
      (Sentry/init (clj->js {:dsn "https://74a424e902fa437ab1a424ac2391ce07@o65834.ingest.sentry.io/5632052"
                          :integrations [(new (. Integrations -BrowserTracing))]
                          :tracesSampleRate 1.0})))
  (case version
    1 (init-version-1)
    2 (init-version-2)))
