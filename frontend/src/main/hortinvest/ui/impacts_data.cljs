(ns hortinvest.ui.impacts-data
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require
   [cljs.core.async :refer [<! chan >!]]
   [clojure.string :refer [triml split]]
   [hortinvest.api :as api]
   [hortinvest.config :refer [main-project project-ids]]
   [reagent.core :as r]))

(def db (r/atom {}))

(def partners (r/atom {}))

(defn parse-projects [projects indicators-parsed]
  (mapv
   (fn [p] (assoc p :indicators (vec (filter (fn [i](= (:id p) (:result i))) indicators-parsed))))
   projects))

(defn parse-indicators [indicators periods]
  (mapv
   (fn [i] (assoc i :periods (vec (filter (fn [ip](= (:indicator ip) (:id i))) periods))))
   indicators))

(defn outcome-level [s]
  ;; TODO: should we throw an exception here?
  (let [r (first (re-find #"^(\d\.?)+" (triml s)))]
    (. js/Number parseInt (first (split r #"\.")))))

;;  (assert (= 1 (outcome-level "1.3.4 Trained RAB 88989")))
;;  (assert (= 5 (outcome-level "5.3.4 Trained RAB 88989")))



(defn partner-indicator-key [impact indicator]
  (str (:title impact) "- - - - -" (:title indicator)))

(defn load-partners
  "build a dictionary with key is the result of `(partner-indicator-key [impact indicator])`
  and value equals to periods list"
  []
  (let [assoc-indicators (fn [container partner-topic]
                           (reduce (fn [c2 indicator]
                                     (assoc c2 (partner-indicator-key partner-topic indicator)
                                            (:periods indicator)))
                                   container (:indicators partner-topic)))
        res (reduce (fn [dict [partner partner-topics]]
                      (assoc dict partner
                             (reduce (fn [c partner-topic]
                                       (let [res (assoc-indicators c partner-topic)]
                                         (if-let [outputs (:outputs partner-topic)]
                                           (reduce assoc-indicators res outputs)
                                           res)))
                                     dict partner-topics)))
                    {}
                    (dissoc @db main-project))]
    (reset! partners res)))

(def control-chan (chan))

(def api-error (r/atom []))

(let [loaded (atom 0)]
  (go-loop []
    (let [_ (<! control-chan)]
      (swap! loaded inc)
      (if (= (count project-ids) @loaded)
        (load-partners)
        (recur)))))

(defn load
  ([]
   (doall (map load project-ids)))
  ([project-data]
   (go (let [read-chan (fn [c]
                         (go (let [data (<! c)]
                               (if (= :error (first data))
                                 (do
                                   (swap! api-error conj data)
                                   (throw (ex-info "api error" data))
                                   nil)
                                 data))))
             project-id (:id project-data)
             periods-chan (api/load-rec :indicator-periods (api/indicator-periods-url project-id))
             indicators-chan (api/load-rec :indicators (api/indicators-url project-id))
             projects-chan (api/load-rec :projects (api/projects-url project-id))
             periods (<! (read-chan periods-chan))
             indicators (<! (read-chan indicators-chan))
             projects (<! (read-chan projects-chan))]
         (when (and periods indicators projects)
           (let [parsed-indicators (parse-indicators indicators periods)
                 projects-parsed (parse-projects projects parsed-indicators)
                 projects-by-type (group-by :type  projects-parsed)
                 indicators* (get projects-by-type "3")
                 outputs (->> (get projects-by-type "1")
                              (map #(assoc % :outcome-level (outcome-level (:title %)))))
                 outcomes (->> (get projects-by-type "2")
                               (map (fn [o]
                                      (let [ol (outcome-level (:title o))]
                                        (assoc o :outputs (vec (filter #(= ol (:outcome-level %)) outputs)))))))]
             (swap! db (fn [x]  (assoc x project-data
                                       (-> []
                                           (into indicators*)
                                           (into outcomes)))))
             (>! control-chan project-data)))))))
