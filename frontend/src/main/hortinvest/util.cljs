(ns hortinvest.util)

(defn menu-change [app-state event]
  (let [{:strs [keyPath]} (js->clj event)]
    (when (not= keyPath (:current-page @app-state))
      (swap! app-state assoc :current-page keyPath))))
