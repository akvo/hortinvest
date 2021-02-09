(ns hortinvest.ui.impacts-data
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require
   [reagent.core :as r]
   [reagent.dom :as rdom]
   [cljs-http.client :as http]
   [cljs.core.async :refer [<! chan >!]]
   [clojure.string :refer [includes? triml split]]
   [syn-antd.col :refer [col]]
   [syn-antd.progress :refer [progress]]
   [syn-antd.row :refer [row]]))

(def host (.(. js/window -location) -host))

(def development? (or (includes? host "localhost") (includes? host "akvotest")))

(def api-domain (if (includes? host "akvotest")
                  "https://rsr.test.akvo.org/rest"
                  "https://rsr.akvo.org/rest"))

(defn api [v s]
  (str api-domain "/v" v "/" s))

(def db (r/atom {}))

(def partners (r/atom {}))

(defn indicator-periods-url [project-id]
  (if development?
    (str "./jsons/" project-id "-indicator-periods.json")
    (api 1 (str "indicator_period/?format=json&limit=100&indicator__result__project=" project-id))))

(defn indicators-url [project-id]
  (if development?
    (str "./jsons/" project-id "-indicators.json")
    (api 1 (str "indicator/?format=json&limit=100&result__project=" project-id))))

(defn projects-url [project-id]
  (if development?
    (str "./jsons/" project-id "-results.json")
    (api 2 (str "result/?format=json&limit=100&project=" project-id))))

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
    (. js/Number parseInt (first (split r #"\.")))
    ))

;;  (assert (= 1 (outcome-level "1.3.4 Trained RAB 88989")))
;;  (assert (= 5 (outcome-level "5.3.4 Trained RAB 88989")))
(defn get-keys [tag]
  (condp = tag
    :projects [:id :title :type]
    :indicators [:title :id :result]
    :indicator-periods [:target_value :actual_value :period_start :period_end :indicator]))

(defn load-rec [tag url]
  (go (let [c (http/get url {:with-credentials? false})
            b (:body (<! c))
            results (:results b)]
        (if (:next b)
          (into results (<! (load-rec tag (:next b))))
          (mapv #(select-keys % (get-keys tag)) results)))))

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

(def control-chan (chan))

(def menu (atom {:impacts [] :outcomes []}))

(defn load-partners []
  (println "loading partners")
  (let [data (dissoc @db main-project)]
    (swap! partners (fn []
                      (reduce (fn [c1 [k v]]
                                (assoc c1 k
                                       (reduce (fn [c p]
                                                 (reduce (fn [c2 i]
                                                           (assoc c2 (str (:title p) "- - - - -" (:title i))
                                                                  (:periods i)))
                                                         c (:indicators p)))
                                               c1 v)))
                              {} data))))
  (swap! menu assoc :outcomes (mapv #(select-keys % [:id :title]) (filter #(= "2" (:type %)) (get @db main-project))))
  (swap! menu assoc :impacts (mapv #(select-keys % [:id :title]) (filter #(= "3" (:type %)) (get @db main-project)))))

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
             periods-chan (load-rec :indicator-periods (indicator-periods-url project-id))
             indicators-chan (load-rec :indicators (indicators-url project-id))
             projects-chan (load-rec :projects (projects-url project-id))
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
