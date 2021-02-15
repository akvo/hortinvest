(ns hortinvest.api
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
   [clojure.string :refer [includes?]]
   [cljs.core.async :refer [<!]]
   [cljs-http.client :as http]
   [hortinvest.util :as util]))

(def api-domain (if (includes? util/host "akvotest")
                  "https://rsr.test.akvo.org/rest"
                  "https://rsr.akvo.org/rest"))

(defn api [v s]
  (str api-domain "/v" v "/" s))

(defn indicator-periods-url [project-id]
  (if util/development?
    (str "./jsons/" project-id "-indicator-periods.json")
    (api 1 (str "indicator_period/?format=json&limit=100&indicator__result__project=" project-id))))

(defn indicators-url [project-id]
  (if util/development?
    (str "./jsons/" project-id "-indicators.json")
    (api 1 (str "indicator/?format=json&limit=100&result__project=" project-id))))

(defn projects-url [project-id]
  (if util/development?
    (str "./jsons/" project-id "-results.json")
    (api 2 (str "result/?format=json&limit=100&project=" project-id))))

(defn get-keys [tag]
  (condp = tag
    :projects [:id :title :type]
    :indicators [:title :id :result]
    :indicator-periods [:target_value :actual_value :period_start :period_end :indicator]))

(defn load-rec [tag url]
  (go (let [response (<! (http/get url {:with-credentials? false}))]
        (if (contains? http/unexceptional-status? (:status response))
          (let [b (:body response)
                results (:results b)]
            (if (:next b)
              (into results (<! (load-rec tag (:next b))))
              (mapv #(select-keys % (get-keys tag)) results)))
          [:error (:status response) response]))))
