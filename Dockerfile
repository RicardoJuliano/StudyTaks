FROM gradle:8.10-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle :backend:installDist --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/backend/build/install/backend /app
EXPOSE 8080
CMD ["/app/bin/backend"]