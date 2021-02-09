(ns hortinvest.ui.impacts-data
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require
   [cljs.core.async :refer [<! chan >!]]
   [clojure.string :refer [triml split]]
   [hortinvest.util :as util]
   [hortinvest.api :as api]
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

(def main-project {:title "Hortinvest (Original)"
                   :id 7218})

(def project-ids [main-project
                  {:title "SNV Rwanda - HortInvest"
                   :id 9559}
                  {:title "Holland Greentech / MACAMPO - HortInvest"
                   :id 9558}
                  {:title "IDH Sustainable Trade Initiative - HortInvest"
                   :id 9557}
                  {:title "Wageningen University & Research - HortInvest"
                   :id 9556}
                  {:title "Agriterra - HortInvest"
                   :id 9555}])

(def menu (r/atom {:impacts [] :outcomes []}))

(defn partner-indicator-key [impact indicator]
  (str (:title impact) "- - - - -" (:title indicator)))

(defn load-partners []
  (println "loading partners")
  (let [data (dissoc @db main-project)]
    (swap! partners (fn []
                      (reduce (fn [c1 [k v]]
                                (assoc c1 k
                                       (reduce (fn [c p]
                                                 (reduce (fn [c2 i]
                                                           (assoc c2 (partner-indicator-key p i)
                                                                  (:periods i)))
                                                         c (:indicators p)))
                                               c1 v)))
                              {} data))))

  (let [outcomes (filter #(= "2" (:type %)) (get @db main-project))
        impacts (filter #(= "3" (:type %)) (get @db main-project))]
    (reset! menu {:outcomes outcomes :impacts impacts})))

(def control-chan (chan))

(let [loaded (atom 0)]
  (go-loop []
         (let [data (<! control-chan)]
           (swap! loaded inc)
           (print @loaded data)
           (if (= (count project-ids) @loaded)
             (load-partners)
             (recur)))))

(defn load
  ([]
   (doall (map load project-ids)))
  ([project-data]
   (go (let [project-id (:id project-data)
             periods-chan (api/load-rec :indicator-periods (api/indicator-periods-url project-id))
             indicators-chan (api/load-rec :indicators (api/indicators-url project-id))
             projects-chan (api/load-rec :projects (api/projects-url project-id))
             periods (<! periods-chan)
             indicators (<! indicators-chan)
             parsed-indicators (parse-indicators indicators periods)
             projects-parsed (parse-projects (<! projects-chan) parsed-indicators)
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
         (>! control-chan project-data)))))
