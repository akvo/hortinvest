(ns hortinvest.app
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]))

(def rsr-api-url "http://localhost:8080/rsr-response.json")

(defn init []
  (println "Hello World"))


(comment

  (go (let [response (<! (http/get rsr-api-url
                                   {:with-credentials? false
                                    ;; :query-params {"since" 135}
                                    }))]
        (prn (:status response))
        (prn (:body response))
        ))

  )
