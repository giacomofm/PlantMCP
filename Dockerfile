FROM maven:3-eclipse-temurin-25 AS build
WORKDIR /build
COPY . .
RUN mvn -B package

FROM eclipse-temurin:25-jre
RUN mkdir /data
VOLUME /data
WORKDIR /app
COPY --from=build /build/target/plantmcp.jar plantmcp.jar
ENTRYPOINT ["java", "-DPLANTMCP_DOCKER=true", "-jar", "plantmcp.jar"]
