(ns hortinvest.ui.menu
  (:require
   [hortinvest.ui.impacts :as i]
   [reitit.frontend.easy :as rfe]
   [syn-antd.layout :refer [layout-header]]
   [syn-antd.menu :refer [menu menu-item]]
   [syn-antd.switch :as switch]))


(defn header-top-menu [app-state]
  (let [path (-> @app-state :route-match :path)
        selected-key (if (.startsWith path "/projects")
                       "/projects"
                       "/results")]
    [menu {:mode "horizontal"
           :selectedKeys [selected-key]
           :theme "light"}
     [menu-item {:key "/results"}
      [:a {:href (rfe/href :impact)} "Results"]]
     [menu-item {:key "/projects"}
      [:a {:href (rfe/href :project-list)} "Projects"]]]))

(defn project-menu-item [{:keys [id title]}]
  [menu-item {:key (str "/projects/" id)}
   [:a {:href (rfe/href :project {:id id})} title]])

(defn projects-menu [app-state]
  (let [{:keys [config route-match]} @app-state]
    [menu {:mode "horizontal"
           :selectedKeys [(:path route-match)]
           :style {:line-height "32px"}
           :theme "light"}
     (for [config (:projects config )]
       (project-menu-item config))]))

(defn outcome-menu-item [{:keys [id title]}]
  [menu-item {:key (str "/results/outcomes/" id)}
   [:a {:href (rfe/href :outcome {:id id})} title]])

(defn results-menu [app-state]
  (i/load-projects)
  (let [state @app-state
        outcome-configs (-> state :config :results :outcomes)]
    [:div
     [:div {:class "ant-menu-horizontal"
            :style {:float "right"
                    :lineHeight "32px"
                    :borderBottom "0"}}
      [:div {:style {:marginRight "10px"}}
       [:span  "Percentages"]
       [switch/switch
        {:checked (-> @app-state :switches :percentages?)
         :style {:marginRight "30px"
                 :marginLeft "10px"}
         :on-change #(swap! app-state update-in [:switches :percentages?] not (js->clj %))
         :size "small"}]
       [:span  "Contributors"]
       [switch/switch
        {:checked (-> @app-state :switches :disaggregated?)
         :style {:marginLeft "10px"}
         :on-change #(swap! app-state update-in [:switches :disaggregated?] not (js->clj %))
         :size "small"}]
       ]]
     [menu {:mode "horizontal"
            :selectedKeys [(-> state :route-match :path)]
            :style {:line-height "32px"
                    :border-bottom-color "#fff"}
            :theme "light"}
      [menu-item {:key "/results/impact"}
       [:a {:href (rfe/href :impact)} "Impact"]]
      (for [config outcome-configs]
        (outcome-menu-item config))]]))

(defn header-sub-menu [app-state]
  (let [state @app-state
        path (-> state :route-match :path)]
    (cond
      (.startsWith path "/projects") (projects-menu app-state)
      (.startsWith path "/results") (results-menu app-state))))

(defn header [app-state]
  [layout-header
   {:style {:backgroundColor "#fff"
            :position "fixed"
            :width "100%"
            :zIndex 1
            :height 100}}
   [:div {:class "logo"}
    [:h1 {:style {:color "#222"}} "Hortinvest"]]
   [header-top-menu app-state]
   [header-sub-menu app-state]])
