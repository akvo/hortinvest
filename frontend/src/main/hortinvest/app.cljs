(ns hortinvest.app
  (:require
   [clojure.string :refer [includes?]]
   ["@sentry/browser" :as Sentry]
   ["@sentry/tracing" :as tracing :refer [Integrations]]
   [hortinvest.config :as config]
   [hortinvest.util :as util]
   [hortinvest.ui :as ui]
   [hortinvest.routes :as routes]
   [reagent.core :as r]
   [reagent.dom :as rdom]
   [reitit.frontend :as rf]
   [reitit.frontend.easy :as rfe]))



(defonce app-state
  (r/atom {:config config/config
           :route-match nil
           :switches {:percentages? true
                      :disaggregated? false}}))

(defn init []
  (when-not (includes? util/host "localhost")
    (Sentry/init (clj->js {:dsn "https://74a424e902fa437ab1a424ac2391ce07@o65834.ingest.sentry.io/5632052"
                           :integrations [(new (. Integrations -BrowserTracing))]
                           :tracesSampleRate 1.0})))
  (rfe/start!
   (rf/router routes/routes {:conflicts nil
                             :data {:config config/config}})
   (fn [m]
     (swap! app-state #(assoc % :route-match m)))
   {:use-fragment true})
  (rdom/render [ui/root app-state]
               (.getElementById js/document "app")))
