FROM openjdk:8-jre-alpine

LABEL maintainer="ociubotaru@developmentgateway.org"

ARG PULL_REQUEST
ARG BRANCH
ARG IMPORTER_JAR
ARG AMP_URL
ARG IATI_ID_FIELD
ARG PROCESSOR_VERSION
ARG COUNTRY

RUN mkdir -p /opt/importer
WORKDIR /opt/importer

# install bash to be able to run wait-for-it.sh
RUN apk add --no-cache bash gawk sed grep bc coreutils

ADD wait-for-it.sh .
ADD application.yml .

EXPOSE 8081

ENTRYPOINT ./wait-for-it.sh db:5432 -t 0 -- \
        mkdir -p /opt/importer/data/tmp && \
        mkdir -p /opt/importer/data/files && \
        java \
        -Djava.io.tmpdir=/opt/importer/data/tmp \
        -DActivityFetcher.fileStore=/opt/importer/data/files \
        -DAMPStaticProcessor.baseURL=$AMP_URL \
        -DAMPStaticProcessor.ampIatiIdField==$IATI_ID_FIELD \
        -DAMPStaticProcessor.processor_version=$PROCESSOR_VERSION \
        -DIATIProcessor.default_country=$COUNTRY \
        -XX:HeapDumpPath=/opt/heapdumps -XX:+HeapDumpOnOutOfMemoryError \
        -jar importer.jar

LABEL "pull-request"=$PULL_REQUEST
LABEL "branch"=$BRANCH

ADD $IMPORTER_JAR importer.jar
