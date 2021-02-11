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


(defn grid-opts [opts & [style-opts color]]
  (if false #_(includes? host "localhost")
    (merge (merge {:style (merge
                          {:border 1 :border-style "solid"}
                          {:border-color (or color "gray")}
                          style-opts)})
           opts)
    (merge {:style style-opts} opts)))

(def piwik* (atom nil))

(defn piwik []
  (or @piwik*
      (when-let [p (. js/window -Piwik)]
        (reset! piwik* (. p getTracker (if (includes? host "akvotest")
                                         "https://hortinvest.akvotest.org"
                                         "https://hortinvest.akvo.org")
                          "65b7a0f2-16a4-43c0-bde5-fd4bcf2231ac")))))

(defn track-page-view [s]
  (when-let [^js/Tracker p (piwik)]
    (println "track-page-view" s)
    (. p setDocumentTitle s)
    (. p trackPageView)))
