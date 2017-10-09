FROM node:8.6-alpine

# directory automatically created
WORKDIR /usr/nlidb

# install dependencies packages first to utilize docker layer caching
# COPY dest path could be relative to WORKDIR, or absolute. dest dir must end with /
COPY client/package.json client/
RUN cd client && npm install

# copy everything to filesystem of container
COPY client client/

# build react bundle
RUN cd client && npm run build


FROM frolvlad/alpine-oraclejdk8:full
WORKDIR /usr/nlidb
COPY src src
COPY gradle gradle
COPY gradlew ./
COPY build.gradle ./
COPY --from=0 /usr/nlidb/client/build/ src/main/resources/public/

EXPOSE 80
CMD ["./gradlew", "-Dspring.profiles.active=prod", "bootRun"]
