FROM alpine
RUN apk add --no-cache openssl
RUN apk add --no-cache curl
RUN apk add --no-cache nodejs
RUN apk add openjdk11
#RUN apk add py3-pip
#RUN apk add gcc musl-dev python3-dev libffi-dev openssl-dev cargo make
#RUN pip install --upgrade pip
#RUN pip install azure-cli
COPY client-key1.pem .
COPY client-cert1.pem .
COPY pim-d-sql-kv-pim-d-kafka-consumer-cert-20220902.pfx .
COPY src/main/resources/keystore.jks .
COPY src/main/resources/truststore.jks .
COPY build/libs/sijukafkachecktemp-1.0.0-SNAPSHOT-all.jar .
COPY selfsignedInKeystore.jks .
COPY keystorefromkubernetes.jks .
