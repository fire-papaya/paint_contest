FROM eclipse-temurin:17

RUN mkdir /opt/app
COPY bot/build/libs/bot-0.4.1.jar /opt/app
WORKDIR /opt/app
CMD ["java", "-Dspring.profiles.active=stage", "-jar", "bot-0.4.1.jar"]