FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY . .
RUN chmod +x gradlew
RUN ./gradlew :backend:installDist --no-daemon --configure-on-demand

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/backend/build/install/backend /app
EXPOSE 8080
CMD ["/app/bin/backend"]in/backend"]