# HortInvest

A dashboard consisting of a client application presenting data from Akvo Flow,
Lumen & RSR.

Development is done by local development without docker. Push to main will
deploy to test. Run promote script to "release" to production.

``` bash
$ promote-test-to-prod.sh
```

## Local development
Since the dashboard pulls data from Akvo RSR we have a remote mock API.

### Mock RSR APi (webserver)
using https://www.npmjs.com/package/http-server
1. install it https://www.npmjs.com/package/http-server#installation
2. go to the static json folder
3. `http-server -p 50000 --cors`


### frontend

``` bash
cd frontend
npm install
clj -A:dev:test -M:shadow-cljs watch main
...
shadow-cljs - HTTP server available at http://localhost:8080
shadow-cljs - server version: 2.11.11 running at http://localhost:9630
shadow-cljs - nREPL server started on port 50042
```

Browse to http://localhost:8080/


### Emacs Cider
Example setup
``` bash
cat ~/.clojure/deps.edn
{:aliases {:cider {:extra-deps {cider/cider-nrepl {:mvn/version "0.25.8"}}}}}
clj -A:dev:test:cider -M:shadow-cljs watch main
```

### Test production docker image.

Build and run production image
``` bash
./ci/build.sh
./ci/run.sh
```

http://localhost:8080

Stop & clean up
``` bash
./ci/stop.sh
```

## Auto update lumen datasets

There is a K8s cron job logic in this [folder/branch](https://github.com/akvo/hortinvest/tree/automatic-updates/lumen-datasets-periodic-updates)

To be deployed we just need to commit to this [branch](https://github.com/akvo/hortinvest/tree/automatic-updates/)

The deployment is in production

In this [lines](https://github.com/akvo/hortinvest/blob/automatic-updates/lumen-datasets-periodic-updates/update.py#L25-L37) is the list of datasets to be updated

The current periodicity is [daily](https://github.com/akvo/hortinvest/commit/8833123c3aaa3b68adc20a7d2cab546bfb029f7b)
