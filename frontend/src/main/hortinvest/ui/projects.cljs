(ns hortinvest.ui.projects
  (:require
   [goog.string :as gstring]
   [goog.string.format]
   [hortinvest.util :as util]
   [syn-antd.col :refer [col]]
   [syn-antd.menu :refer [menu menu-item]]
   [syn-antd.row :refer [row]]))


(defn iframe-html [{:keys [height src]}]
  (gstring/format "<iframe width='100%' height='%spx' src='%s' frameborder='0' allow='encrypted-media'></iframe>"
                  height
                  src))

(defn iframe [config]
  [:div {:dangerouslySetInnerHTML {:__html (iframe-html config)}}])

(defn projects-menu [app-state dashboard-config]
  (reduce (fn [menu {:keys [id title]}]
            (conj menu
                  [menu-item {:key id :title id} [:a title]]))
          [menu {:mode "horizontal"
                 :defaultSelectedKeys [(:projects-menu-selection @app-state)]
                 :onClick #(util/menu-change app-state :projects-menu-selection %)}]
          dashboard-config))

(defn projects [app-state dashboard-config]
  [row
   [col {:span 24}
    [projects-menu app-state dashboard-config]
    [iframe (first (filter #(= (:id %) (:projects-menu-selection @app-state))
                           dashboard-config))]]])
