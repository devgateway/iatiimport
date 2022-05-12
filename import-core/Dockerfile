FROM maven:3.8.4-jdk-8 as build
ARG BUILD_SOURCE
WORKDIR /tmp/build
COPY . .
RUN --mount=type=cache,target=/root/.m2 --mount=type=cache,target=/root/.npm \
    mvn -B clean package -DskipTests -DqaBuild -DbuildSource=$BUILD_SOURCE \
    && mv import-ui/target/import-ui.jar /tmp/import-ui.jar \
    && rm -r *.*

FROM openjdk:8-jre

LABEL maintainer="ociubotaru@developmentgateway.org"

ARG PULL_REQUEST
ARG BRANCH

RUN mkdir -p /opt/importer
WORKDIR /opt/importer

COPY wait-for-it.sh .

EXPOSE 8081

ENTRYPOINT ./wait-for-it.sh db:5432 -t 0 -- \
        mkdir -p /opt/importer/data/tmp && \
        mkdir -p /opt/importer/data/files && \
        java \
        -Djava.io.tmpdir=/opt/importer/data/tmp \
        -DActivityFetcher.fileStore=/opt/importer/data/files \
        -DAMPStaticProcessor.baseURL=$AMP_URL \
        -DAMPStaticProcessor.processor_version=$PROCESSOR_VERSION \
        -DIATIProcessor.default_country=$COUNTRY \
        -XX:HeapDumpPath=/opt/heapdumps -XX:+HeapDumpOnOutOfMemoryError \
        -jar importer.jar

LABEL "pull-request"=$PULL_REQUEST
LABEL "branch"=$BRANCH

COPY --from=build /tmp/import-ui.jar importer.jar
