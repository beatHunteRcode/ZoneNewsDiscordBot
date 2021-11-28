# temp container to build using gradle
FROM gradle:5.5.0-jdk8 AS TEMP_BUILD_IMAGE
ENV APP_HOME=/app/
WORKDIR $APP_HOME
COPY input .
COPY build.gradle settings.gradle $APP_HOME
RUN gradle build
COPY . .
RUN gradle clean build

#Creating Docker Image for Java App
FROM openjdk:8
ENV ARTIFACT_NAME=ZoneNewsDiscordBot-1.0-SNAPSHOT.jar
ENV APP_HOME=/app/

ENV DISCORD_BOT_API_TOKEN="_____.4AsPZJmmJoc0t4rZbLK0EGjey84"
ENV INST_LOGIN="***"
ENV INST_PASSWORD="***"

WORKDIR $APP_HOME
COPY --from=TEMP_BUILD_IMAGE $APP_HOME/build/libs/$ARTIFACT_NAME .
ENTRYPOINT ["java", "-jar", "-DDISCORD_BOT_API_TOKEN=${DISCORD_BOT_API_TOKEN}", "-DINST_LOGIN=${INST_LOGIN}", "-DINST_PASSWORD=${INST_PASSWORD}", "ZoneNewsDiscordBot-1.0-SNAPSHOT.jar"]

#--helpful command for run app from docker--
#docker run --rm -it --volume D:\Programming\Docker\ZoneNewsDiscordBot:/opt --workdir=/opt --env SOMEKEY=botapitoken openjdk:8 java -jar myJar.jar
# docker run --rm -it --volume /app:/opt --workdir=/opt --env DISCORD_BOT_API_TOKEN=NjM2OTg4NzAyMjI0MDIzNTg0.XbHn9Q.4AsPZJmmJoc0t4rZbLK0EGjey84 --env INST_LOGIN=zndsbfguf --env INST_PASSWORD=sherepa2000spb openjdk:8 java -jar ZoneNewsDiscordBot.jar
