# Stage 1: build con Maven y JDK 21
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copiar wrapper y POM primero para aprovechar cache de capas
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -B -DskipTests dependency:go-offline

# Copiar el codigo y empaquetar
COPY src ./src
RUN ./mvnw -B -DskipTests clean package

# Stage 2: runtime con JRE 21
FROM eclipse-temurin:21-jre
WORKDIR /app

# Render asigna PORT dinamicamente; lo respetamos
ENV PORT=8080
EXPOSE 8080

COPY --from=build /app/target/*.jar app.jar
COPY entrypoint.sh /app/entrypoint.sh
RUN chmod +x /app/entrypoint.sh

ENTRYPOINT ["/app/entrypoint.sh"]
