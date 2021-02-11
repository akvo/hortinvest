(ns hortinvest.ui2.util
  (:require
   [reagent.core :as r]
   [reitit.frontend.easy :as rfe]))


;; redirect! & Redirect from Reitit examples
;; https://github.com/metosin/reitit/blob/
;; c4e84c2875fa4b53a6eae1a2ce0236b75f856277/examples/frontend-links/src/
;; frontend/core.cljs#L23-L38

(defn redirect!
  "If `push` is truthy, previous page will be left in history."
  [{:keys [to path-params query-params push]}]
  (if push
    (rfe/push-state to path-params query-params)
    (rfe/replace-state to path-params query-params)))

(defn Redirect
  "Component that only causes a redirect side-effect."
  [_]
  (r/create-class
   {:component-did-mount  (fn [this] (redirect! (r/props this)))
    :component-did-update (fn [this [_ prev-props]]
                            (when (not= (r/props this) prev-props)
                              (redirect! (r/props this))))
    :render (fn [_] nil)}))

(defn redirect-to-default-page [m]
  [Redirect {:to :project
             :path-params {:id (-> m
                                   :data
                                   :config
                                   :projects
                                   first
                                   :id)}}])
