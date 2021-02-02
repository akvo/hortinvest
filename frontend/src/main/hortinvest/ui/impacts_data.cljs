(ns hortinvest.ui.impacts-data
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
   [reagent.core :as r]
   [reagent.dom :as rdom]
   [cljs-http.client :as http]
   [cljs.core.async :refer [<!]]
   [clojure.string :refer [includes? triml split]]
   [syn-antd.col :refer [col]]
   [syn-antd.progress :refer [progress]]
   [syn-antd.row :refer [row]]))

(def host (.(. js/window -location) -host))

(def development? (includes? host "localhost"))

(def api-domain "https://rsr.akvo.org/rest")

(def project-id 7218)

(defn api [v s]
  (str api-domain "/v" v "/" s))

(def db (r/atom []))

(def indicator-periods-url
  (if development?
    "http://localhost:50000/periods.json"
    (api 1 (str "indicator_period/?format=json&limit=100&indicator__result__project=" project-id))))

(def indicators-url
  (if development?
    "http://localhost:50000/indicators.json"
    (api 1 (str "indicator/?format=json&limit=100&result__project=" project-id))))

(def projects-url
  (if development?
    "http://localhost:50000/result.json"
    (api 2 (str "result/?format=json&limit=100&project=" project-id))))

(defn parse-projects [projects indicators-parsed]
  (mapv
   (fn [p] (assoc p :indicators (vec (filter (fn [i](= (:id p) (:result i)))  indicators-parsed))))
   projects))

(defn parse-indicators [indicators periods]
  (mapv
   (fn [i] (assoc i :periods (vec (filter (fn [ip](= (:indicator ip) (:id i))) periods))))
   indicators))

(defn outcome-level [s]
  ;; TODO: should we throw an exception here?
  (let [r (first (re-find #"^(\d\.?)+" (triml s)))]
    (. js/Number parseInt (first (split r #"\.")))
    ))

;;  (assert (= 1 (outcome-level "1.3.4 Trained RAB 88989")))
;;  (assert (= 5 (outcome-level "5.3.4 Trained RAB 88989")))

(defn load-rec [url]
  (go (let [c (http/get url {:with-credentials? false})
            b (:body (<! c))
            results (:results b)]
        (if (:next b)
          (into results (<! (load-rec (:next b))))
          results))))
(defn load []
  (go (let [periods-chan (load-rec indicator-periods-url)
            indicators-chan (load-rec indicators-url)
            projects-chan (load-rec projects-url)
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
        (swap! db (fn []  (-> []
                              (into indicators*)
                              (into outcomes)))))))
