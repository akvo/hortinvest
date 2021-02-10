(ns hortinvest.app
  (:require
   [hortinvest.config :as config]
   [hortinvest.ui :as ui]
   [reagent.core :as r]
   ["@sentry/tracing" :as tracing :refer [Integrations]]
   ["@sentry/browser" :as Sentry]

   [reagent.dom :as rdom]))



(defonce app-state
  (r/atom {:current-page ["results"]
           :projects {:config config/dashboard-config}}))

(defn init []
  (Sentry/init (clj->js {:dsn "https://74a424e902fa437ab1a424ac2391ce07@o65834.ingest.sentry.io/5632052"
                         :integrations [(new (. Integrations -BrowserTracing))]
                         :tracesSampleRate 1.0}))
  (rdom/render [ui/root app-state]
               (js/document.getElementById "app")))
