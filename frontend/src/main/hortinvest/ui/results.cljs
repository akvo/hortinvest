(ns hortinvest.ui.results
  (:require
   [hortinvest.ui.results.impacts :refer [impacts]]
   [hortinvest.ui.results.outcomes :refer [outcomes]]))


(defn results [app-state]
  (case (-> @app-state :current-page first)
    "outcomes" (outcomes)
    (impacts)))
