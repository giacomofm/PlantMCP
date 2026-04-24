FROM eclipse-temurin:25-jdk AS build
WORKDIR /build
COPY . .
RUN ./mvnw -B package -DskipTests

FROM eclipse-temurin:25-jre
RUN mkdir /data
VOLUME /data
WORKDIR /app
COPY --from=build /build/target/plantmcp.jar plantmcp.jar
ENTRYPOINT ["java", "-DPLANTMCP_DOCKER=true", "-jar", "plantmcp.jar"]
