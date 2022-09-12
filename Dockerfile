FROM alpine
COPY src/main/resources/abnrootcertificate.pem /usr/local/share/ca-certificates/abn-cert.crt
RUN cat /usr/local/share/ca-certificates/abn-cert.crt >> /etc/ssl/certs/ca-certificates.crt
RUN apk add --no-cache openssl 
RUN apk add --no-cache curl 
RUN apk add --no-cache nodejs 
RUN apk add --no-cache openjdk11 
#RUN apk add py3-pip 
#RUN apk add gcc musl-dev python3-dev libffi-dev openssl-dev cargo make
#RUN pip install --upgrade pip
#RUN pip install azure-cli
RUN mkdir -p /code
WORKDIR /code
COPY keystore.jks .
COPY truststore.jks .
#COPY pim-d-sql-kv-pim-d-kafka-consumer-cert-20220902.pfx .
#COPY src/main/resources/keystore.jks .
#COPY src/main/resources/truststore.jks .
COPY build/libs/sslconnectiondebugging-1.0.0-SNAPSHOT-all.jar .
COPY src/main/resources/selfsignedInKeystore.jks .
#COPY keystorefromkubernetes.jks .
