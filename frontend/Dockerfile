FROM node:alpine AS cljs-builder
ENV CLOJURE_VER=1.10.2.774
WORKDIR /app
COPY home_deps.edn /root/.clojure/deps.edn
COPY . /app
RUN set eux; \
        apk add --update --no-cache openjdk8 openssl bash curl && \
        npm install -g shadow-cljs && \
        npm install && \
        curl -s https://download.clojure.org/install/linux-install-$CLOJURE_VER.sh | bash && \
        npx shadow-cljs release main && \
        mkdir -p release/js/main/ && \
        cp /app/resources/main/public/index.html ./release && \
        cp /app/resources/main/public/style.css ./release && \
        cp /app/resources/main/public/js/main/main.js ./release/js/main && \
        cp /app/resources/main/public/js/main/manifest.edn ./release/js/main && \
        mkdir -p release/jsons/ && \
        cp /app/resources/main/public/jsons/* ./release/jsons/

FROM nginx:stable-alpine
COPY --from=cljs-builder /app/release /usr/share/nginx/html
