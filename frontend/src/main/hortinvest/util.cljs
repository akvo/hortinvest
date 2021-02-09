(ns hortinvest.util
  (:require
   [clojure.string :refer [includes?]]))

(defn menu-change [app-state event]
  (let [{:strs [keyPath]} (js->clj event)]
    (when (not= keyPath (:current-page @app-state))
      (swap! app-state assoc :current-page keyPath))))

(defn nan [x]
  (if (js/isNaN x) 0 x))

(defn to-int [x]
  (. js/Number parseInt x))

(def host (.(. js/window -location) -host))

(def development? (or (includes? host "localhost") (includes? host "akvotest")))
