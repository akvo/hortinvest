(ns hortinvest.routes
  (:require
   [hortinvest.ui.impacts :as impacts]
   [hortinvest.ui :as ui]))


(def routes
  [["/" {:name :root :view ui/root-page}]
   ["/intervention-areas"
    ["" {:name :project-list :view ui/project-list-page}]
    ["/:id" {:name :project :view ui/project-page}]]
   ["/results"
    ["" {:name :results :view ui/results-page}]
    ["/impact" {:name :impact :view impacts/impacts}]
    ["/outcomes"
     ["" {:name :outcome-list :view ui/outcome-list-page}]
     ["/:id" {:name :outcome :view impacts/outcomes}]]]
   ["/{*path}" {:name :not-found-page :view ui/not-found-page}]])
