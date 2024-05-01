FROM openjdk:17-oracle

COPY build/libs/tictactoe-0.0.1.jar /app/tictactoe.jar

CMD ["java", "-jar", "/app/tictactoe.jar", "--spring.profiles.active=${SPRING_PROFILES_ACTIVE}"]
