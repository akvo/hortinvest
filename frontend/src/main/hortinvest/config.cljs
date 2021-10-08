(ns hortinvest.config)



(def dashboard-config
  [{:height "1000"
    :id "1"
    :src "https://hortinvest.akvolumen.org/s/7jbe6xb2l1E"
    :title "Demo Site"}
   {:height "1000"
    :id "2"
    :src "https://hortinvest.akvolumen.org/s/F6UPzdpVkHs"
    :title "Farmer group"}
   {:height "1000"
    :id "3"
    :src "https://hortinvest.akvolumen.org/s/aI2UtX3Mhio"
    :title "Cooperatives"}
   {:height "1000"
    :id "5"
    :src "https://hortinvest.akvolumen.org/s/534M4nFv6TE"
    :title "Business cases"}
   {:height "1000"
    :id "4"
    :src "https://hortinvest.akvolumen.org/s/yBs6eu3KLzA"
    :title "Other SMEs"}
   {:height "1000"
    :id "6"
    :src "https://hortinvest.akvolumen.org/s/2X0tZojm71g"
    :title "Fruit tree registration"}
   {:height "1000"
    :id "7"
    :src "https://hortinvest.akvolumen.org/s/eyq63eE0SXM"
    :title "Fruit tree monitoring"}
   {:height "1000"
    :id "8"
    :src "https://hortinvest.akvolumen.org/s/JEBKlzrgdag"
    :title "Production"}
   {:height "1000"
    :id "9"
    :src "https://hortinvest.akvolumen.org/s/ZIXUeTwIBYo"
    :title "Sales"}
   {:height "1000"
    :id "10"
    :src "https://hortinvest.akvolumen.org/s/d6pZ0NRyMnU"
    :title "Meetings"}
   {:height "1000"
    :id "11"
    :src "https://hortinvest.akvolumen.org/s/NRuVFl77ijM"
    :title "Training"}])

(def outcomes
  [{:title "Outcome 1"
    :id "1"}
   {:title "Outcome 2"
    :id "2"}
   {:title "Outcome 3"
    :id "3"}
   {:title "Outcome 4"
    :id "4"}])

(def main-project {:title "Hortinvest (Original)"
                   :id 9559})

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
(def config
  {:projects dashboard-config
   :results {:main-project main-project
             :outcomes outcomes
             :project-ids project-ids}})
