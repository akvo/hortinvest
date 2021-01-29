(ns hortinvest.util)

(defn menu-change [app-state menu-key event]
  (let [{:strs [key]} (js->clj event)]
    (when (not (= key (get @app-state menu-key)))
      (swap! app-state assoc menu-key key))))
