FROM node:alpine

ENV CLOJURE_VER=1.10.1.492

WORKDIR /app

COPY package.json .
COPY shadow-cljs.edn .
COPY deps.edn .

RUN set eux; \
        apk add --update --no-cache openjdk8 openssl bash curl && \
        npm install -g shadow-cljs && \
        curl -s https://download.clojure.org/install/linux-install-$CLOJURE_VER.sh | bash

ENTRYPOINT ["npx"]
