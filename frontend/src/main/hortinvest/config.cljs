(ns hortinvest.config)

(def dashboard-config
  [{:id "1"
    :src-id "60117663-3feb-4b48-a44a-a02b6961a9bc"
    :title "Fruit tree & seed distribution"
    :height "2570"
    :src "https://hortinvest.akvolumen.org/s/2X0tZojm71g"}
   {:id "2"
    :src-id "600eec0a-dd6e-46ae-a8d4-53b5fc3ad1a4"
    :title "Business cases"
    :height "5450"
    :src "https://hortinvest.akvolumen.org/s/534M4nFv6TE"}
   {:id "3"
    :src-id "6017f275-874a-4c1e-af0b-98a39b84f019"
    :title "Cooperatives"
    :height "2330"
    :src "https://hortinvest.akvolumen.org/s/aI2UtX3Mhio"}
   {:id "4"
    :src-id "6017f15f-1cb1-497c-adfc-2b25394257d4"
    :title "Demo sites"
    :height "1490"
    :src "https://hortinvest.akvolumen.org/s/7jbe6xb2l1E"}
   {:id "5"
    :src-id "6019998f-ee59-481c-9296-487767e094c5"
    :title "Production and Data collection"
    :height "1000"
    :src "https://hortinvest.akvolumen.org/s/JEBKlzrgdag"}
   {:id "6"
    :src-id "601998f0-061f-4399-b0cb-f57c7e1dab96"
    :title "Events"
    :height "1000"
    :src "https://hortinvest.akvolumen.org/s/XjKn6k2yRmM"}])

(def outcomes
  [{:title "Outcome 1"
    :id "1"}
   {:title "Outcome 2"
    :id "2"}
   {:title "Outcome 3"
    :id "3"}
   {:title "Outcome 4"
    :id "4"}])

(def config
  {:projects dashboard-config
   :results {:outcomes outcomes}})
