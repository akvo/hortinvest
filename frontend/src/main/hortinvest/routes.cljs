(ns hortinvest.routes
  (:require
   [hortinvest.ui2 :as ui2]))

(def routes
  [["/" {:name :root :view ui2/root-page}]
   ["/{*path}" {:name :not-found-page :view ui2/not-found-page}]])
