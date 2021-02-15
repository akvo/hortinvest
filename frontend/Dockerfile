FROM node:lts-alpine3.13 as cljs-builder
ENV CLOJURE_VER=1.10.2.774
ENV CHROME_BIN=/usr/bin/chromium-browser
WORKDIR /app
COPY . /app
RUN set eux; \
        apk add --update --no-cache openjdk8 openssl bash curl chromium && \
        npm i npm@6.13.7 && \
        npm install -g shadow-cljs karma-cli && \
        npm install && \
        curl -s https://download.clojure.org/install/linux-install-$CLOJURE_VER.sh | bash && \
        clojure -M:clj-kondo --lint src && \
        clojure -A:test -M:shadow-cljs compile ci && \
        karma start --single-run && \
        clojure -M:shadow-cljs release main && \
        mkdir -p release/js/main/ && \
        cp /app/resources/main/public/index.html ./release && \
        cp /app/resources/main/public/style.css ./release && \
        cp /app/resources/main/public/js/main/main.js ./release/js/main && \
        cp /app/resources/main/public/js/main/manifest.edn ./release/js/main && \
        mkdir -p release/jsons/ && \
        cp /app/resources/main/public/jsons/* ./release/jsons/

FROM nginx:stable-alpine
COPY nginx/conf.d/default.conf /etc/nginx/conf.d/default.conf
COPY --from=cljs-builder /app/release /usr/share/nginx/html
