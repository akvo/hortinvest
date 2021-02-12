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
clj -A:dev -M:shadow-cljs watch main
...
shadow-cljs - HTTP server available at http://localhost:8080
shadow-cljs - server version: 2.11.11 running at http://localhost:9630
shadow-cljs - nREPL server started on port 50042
```

Browse to http://localhost:8080/


### Emacs Cider
Example setup
``` bash
cat ~/.clojure.deps.edn
{:aliases {:cider {:extra-deps {cider/cider-nrepl {:mvn/version "0.25.8"}}}}}
clj -A:dev:cider -M:shadow-cljs watch main
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
