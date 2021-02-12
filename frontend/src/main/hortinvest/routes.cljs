(ns hortinvest.routes
  (:require
   [hortinvest.ui.impacts :as impacts]
   [hortinvest.ui2 :as ui2]))


(def routes
  [["/" {:name :root :view ui2/root-page}]
   ["/projects"
    ["" {:name :project-list :view ui2/project-list-page}]
    ["/:id" {:name :project :view ui2/project-page}]]
   ["/results"
    ["" {:name :results :view ui2/results-page}]
    ["/impact" {:name :impact :view impacts/impacts}]
    ["/outcomes"
     ["" {:name :outcome-list :view ui2/outcome-list-page}]
     ["/:id" {:name :outcome :view impacts/outcomes}]]]
   ["/{*path}" {:name :not-found-page :view ui2/not-found-page}]])
