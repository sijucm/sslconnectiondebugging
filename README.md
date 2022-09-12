### This file and repository needs major work. This is an initial check in with files used in one effort done in an emergency situation. Hope find time to correct it and make it as a tool that can be used to debug


# sslconnectiondebugging
Various tools to debug ssl connection

#Docker file
The container has tools to debug the SSL connection. This includes openssl tools, nodejs, 
java, keytool, etc

#HTTPS call
The https call package has calls to create ssl context from a keystore and also from
Azure keyvault and call an HTTPS endpoint

#Kafka client
The kafka client is a standalone client that can connect to a kafka broker to check the
connection. This was the original purpose to create this repository and for that reason
this is kept as it is

#Commands that can be used along with a kubernetes environment debugging

## login to the cluster
pimclustername=kubecluster-aks
az aks get-credentials \
--resource-group $resourcegroup \
--name $pimclustername \
--subscription $subscription \
--admin

## connect using the openssl client
openssl s_client -connect serverurl:9092 \
-cert kafka-consumer-cert-20220902.pfx \
-key kafka-consumer-cert-20220902.pfx  \
-state -debug

## to inspect the certificates
openssl s_client \
-showcerts \
-connect distdbk1-wez1.launcher.int.abnamro.com:9092 \
< /dev/null


## docker build of a simple container
docker build -t busybox-openssl .
docker tag busybox-openssl ocadevecr.azurecr.io/oca-commercial_eligibility/busyboxopenssl
docker push ocadevecr.azurecr.io/oca-commercial_eligibility/busyboxopenssl


## download server certificate to local file
openssl s_client -connect distdbk1-wez1.launcher.int.abnamro.com:9092 -showcerts  --verify 5 |  awk '/BEGIN CERTIFICATE/,/END CERTIFICATE/{ if(/BEGIN CERTIFICATE/){a++}; out="cert"a".pem"; print >out}'

## move the above downloade files to a name that matches the certificate
for cert in *.pem; do newname=$(openssl x509 -noout -subject -in $cert | sed -nE 's/.*CN ?= ?(.*)/\1/; s/[ ,.*]/_/g; s/__/_/g; s/_-_/ -/; s/^_//g;p' | tr '[:upper:]' '[:lower:]').pem

## connect to open ssl
openssl s_client -connect distdbk1-wez1.launcher.int.abnamro.com:9092 -CAfile siju/cacerts/abn_amro_bank_infra_ca_g2_et_ou_\=_abn_amro_ciso_o_\=_abn_amro_bank_n_v__l_\=_amsterdam_c_\=_nl.pem -tls1_2 -brief -cert client-cert1.pem -key client-key1.pem

## Run the Docker image created above from a repository. Usedd along with docker file from this repository
kubectl run kafkadebug1 -i --tty --rm --image=ocadevecr.azurecr.io/reponame:v20220902.18 --restart=Never -- sh

## keytool command to import a keystore
keytool -importkeystore -srckeystore ../pim-d-sql-kv-pim-d-kafka-consumer-cert-20220902.pfx -srcstoretype PKCS12 -deststoretype PKCS12 -destkeystore keystore.jks -deststorepass changeit -srcstorepass ""

java \
-cp sijukafkachecktemp-1.0.0-SNAPSHOT-all.jar \
-Djavax.net.debug=all \
com.abnamro.siju.tools.httpscall.ApiClient theUrlToCall keystore.jks

-Djdk.httpclient.HttpClient.log=ssl \
-Djdk.httpclient.HttpClient.log=requests


##add a downloaded certificate to the truststore
keytool -import -file abnca/cert2.pem  -alias firstCA -keystore truststore.jks

##convert keystore type to jks
keytool -importkeystore -destkeystore client_keystore.jks -deststoretype jks -deststorepass changeit -srckeystore keystore.jks  -srcstoretype pkcs12 -srcstorepass changeit

##change keypasswd for key in a keystore
keytool -keypasswd -alias 4f6b4855-0f06-4202-8531-cf0c8fbc6611 -keystore client_keystore.jks -new password1

##change password for key and keystore
keytool -keypasswd -new password1 -keystore client_keystore.jks -storepass changeit -alias 4f6b4855-0f06-4202-8531-cf0c8fbc6611 -keypass password

## kubernetes get a shell to a running pod
kubectl exec --tty --stdin -n service $podname -- /bin/sh


##Service principal create done through the pipeline
# first create a service principal. In some orgs through a pipeline. It does not matter where and which RG
sp_password=$(az ad sp create-for-rbac \
--name http://xyz-pull \
--scope /scope/sdsd/sdsd/sdsds \
--role acrpull \
--query password \
-o tsv)

##get the app Id of the service principal
spAppId=$(az ad sp show --id 040590d0-f189-4918-a80d-14cc963ae2f7 --query appId -o tsv)

## user name is the app id and the passowrd in ABN Amro SP is stored in the keyvault
docker login sanacr1453.azurecr.io  --username $spAppId --password FZs8Q~FH7VFSql3VNzjTJZf-oA3WC2MlH~4pUdvI

az acr build -t sample/spring-boot:v1 -r ocadevecr .

##Probably run it with a pipeline if there is no permission
az role assignment create --assignee 040590d0-f189-4918-a80d-14cc963ae2f7 \
--role acrPull \
--scope $acrregistryid

az role assignment create --assignee 040590d0-f189-4918-a80d-14cc963ae2f7 \
--role acrPush \
--scope $acrregistryid

docker build -t sslconnectiondebugging:v4 .
docker tag sslconnectiondebugging:v4 ocadevecr.azurecr.io/sslconnectiondebugging:v4
docker push ocadevecr.azurecr.io/sslconnectiondebugging:v4

kubectl run kafkadebug1 -i --tty --rm --image=ocadevecr.azurecr.io/spring-app:v2 --restart=Never -- sh

kubectl -n service get secrets custom-keystore -o jsonpath --template '{.data}'
## copy above to separate files for keystore.txt and trusstore.txt
cat keystore.txt | base64 --decode > keystore.jks


## it expects the jks files in the root. Otherwise change the path
java -cp .:sslconnectiondebugging-1.0.0-SNAPSHOT-all.jar  com.siju.tools.kafkaclient.KafkaClientMain

