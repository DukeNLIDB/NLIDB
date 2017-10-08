FROM frolvlad/alpine-oraclejdk8

# directory automatically created
WORKDIR /usr/nlidb

# install dependencies packages first to utilize docker layer caching
# COPY dest path could be relative to WORKDIR, or absolute. dest dir must end with /
COPY client/package.json client/
RUN cd client && npm install

# copy everything to filesystem of container
COPY . ./

# build react bundle
RUN cd client && npm run build
RUN mv client/build/* src/main/resources/public

EXPOSE 80
RUN ./gradlew -Dspring.profiles.active=prod bootRun
