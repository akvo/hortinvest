(ns hortinvest.app
  ;; (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
   ["antd" :as antd]
   ;; ["react" :as react]
   ;; ["react-dom" :as react-dom]
   ;; [cljs-http.client :as http]
   ;; [cljs.core.async :refer [<!]]
   [reagent.core :as r]
   [reagent.dom :as rdom]
   [syn-antd.layout :as layout]
   [syn-antd.menu :as menu]
   ))

;; (def rsr-api-url "http://localhost:8080/rsr/results.json")
;; (def rsr-api-url "http://localhost:8080/rsr/47189.json")

(defonce page-state (r/atom {:selected-key "projects"}))

(defn on-menu-change [args]
  (let [{:strs [item key keyPath domEvent]} (js->clj args)
        {:keys [selected-key]} @page-state]
    (when (not (= key selected-key))
      (swap! page-state assoc :selected-key key))))

(defn projects []
  [:h2 "Projects"])

(defn impacts []
  [:h2 "Impacts"])

(defn reports []
  [:h2 "Reports"])

(defn content [{:keys [selected-key]}]
  (case selected-key
    "impacts" [impacts]
    "reports" [reports]
    [projects]))

(defn root []
  [layout/layout
   [layout/layout-header
    [menu/menu {:mode "horizontal"
                :defaultSelectedKeys ["projects"]
                :onClick on-menu-change}
     [menu/menu-item {:key "projects"
                      :title "projets"}
      [:a "Projects"]]
     [menu/menu-item {:key "impacts"
                      :title "Impacts"}
      [:a "Impacts"]]
     [menu/menu-item {:key "reports"
                      :title "Reports"}
      [:a "Reports"]]]]
   [layout/layout-content
    [content @page-state]]])


(rdom/render [root] (js/document.getElementById "app"))

(defn init []
  (prn "@init"))

(comment

  (go (let [response (<! (http/get rsr-api-url
                                   {:with-credentials? false
                                    ;; :query-params {"since" 135}
                                    }))]
        (prn (:status response))
        (prn (:body response))
        ))

  ;; Raw antd
  #_(react-dom/render
     (.createElement react antd/DatePicker {} )
     (js/document.getElementById "app"))

  )
