# HortInvest Dashboard

## development environment

### dev webserver (mock rsr api)
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

## Release

### Test
Push to the main branch will deploy to the test environment.

### Production
...


Build and run production image
``` bash
./ci/build.sh
./ci/run.sh
```

http://localhost:8080

Stop docker container
``` bash
./ci/stop.sh
```
