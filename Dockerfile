# build stage
FROM gradle:latest AS BUILD
WORKDIR /app
COPY . .
RUN gradle clean build

# production stage
FROM openjdk:latest
ARG key
ENV TOKEN=$key
ENV PREFIX=!
ENV OWNER_ID=197524777458597890
ENV ARTIFACT_NAME=maestro.jar

WORKDIR /app
COPY --from=BUILD . .

ENTRYPOINT exec java -jar ./app/lib/build/libs/$ARTIFACT_NAME

