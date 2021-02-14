(ns hortinvest.util-test
  (:require
   [cljs.test :refer [deftest is run-tests]]
   [hortinvest.util :as util]))

(comment
  (run-tests)
  )

(deftest nan-test
  (is (= (util/nan 1)
         1))
  (is (= (util/nan true)
         true))
  (is (= (util/nan "37,5")
         0))
  (is (= (util/nan (clj->js {}))
         0)))
