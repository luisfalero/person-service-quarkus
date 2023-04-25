# person-service-quarkus

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using: 
```shell script
./mvnw package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/person-service-quarkus-1.0.0-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.

## Provided Code

### RESTEasy Reactive

Easily start your Reactive RESTful Web Services

[Related guide section...](https://quarkus.io/guides/getting-started-reactive#reactive-jax-rs-resources)

# Deploy Openshift

## Variable

```shell script
source ./src/main/resources/util.config
```

## PostgreSQL

```shell script
oc new-project database
```

```shell script
oc -n openshift get templates -o custom-columns=NAME:.metadata.name | grep -i ^postgres
```

```shell script
oc -n openshift process --parameters postgresql-persistent
```

```shell script
oc new-app --template postgresql-persistent \
    -p POSTGRESQL_USER=${POSTGRESQL_USER} \
    -p POSTGRESQL_PASSWORD=${POSTGRESQL_PASSWORD} \
    -p POSTGRESQL_DATABASE=${POSTGRESQL_DATABASE} \
    -p DATABASE_SERVICE_NAME=${DATABASE_SERVICE_NAME} \
    --as-deployment-config
```

```shell script
oc get pods | grep ${DATABASE_SERVICE_NAME} | grep -v -e -deploy -e -build
```

```shell script
oc port-forward pod/postgresql-1-lk87v 15432:5432
```

## Deploy APP Local

```shell script
psql ${POSTGRESQL_DATABASE} ${POSTGRESQL_USER} --host=127.0.0.1 --port=15432
```

```shell script
./mvnw compile quarkus:dev \
  -DDB_USER=${POSTGRESQL_USER} \
  -DDB_PASSWORD=${POSTGRESQL_PASSWORD} \
  -DDB_NAME=${POSTGRESQL_DATABASE} \
  -DDB_HOST=127.0.0.1 \
  -DDB_PORT=15432
```

```shell script
curl http://localhost:8080/person | jq
```

```shell script
curl http://localhost:8080/q/health | jq
```

## Deploy APP Quay.io

```shell script
docker login -u rh_ee_lfalero quay.io
```

```shell script
./mvnw package -DskipTests -Dquarkus.container-image.push=true
```

```shell script
./mvnw package -Pnative -DskipTests -Dquarkus.native.container-build=true
```

## Deploy APP Openshift

```shell script
oc new-project quarkus
```

```shell script
oc create secret generic postgresql-secret \
    --from-literal DB_USER=${POSTGRESQL_USER} \
    --from-literal DB_PASSWORD=${POSTGRESQL_PASSWORD} \
    --from-literal DB_HOST=${DATABASE_HOST} \
    --from-literal DB_PORT=${POSTGRESQL_POST} \
    --from-literal DB_NAME=${POSTGRESQL_DATABASE}
```

```shell script
oc apply -f ./target/kubernetes/openshift.yml
```

```shell script
oc create route edge \
    --service person-service-quarkus \
    --hostname person-service.${OCP4_WILDCARD_DOMAIN}
```

```shell script
curl -k https://person-service.${OCP4_WILDCARD_DOMAIN}/person | jq
```

```shell script
curl -k https://person-service.${OCP4_WILDCARD_DOMAIN}/q/health | jq
```