# Estágio 1: compila o projeto com Maven + JDK 17. Esta imagem é grande
# (tem o JDK inteiro e o Maven), mas ela não vai para produção — é só uma
# "oficina" temporária.
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Estágio 2: imagem final, só com o JRE (não o JDK completo — não precisa
# de compilador para RODAR um .jar já compilado) e o .jar gerado no estágio
# anterior. Isso deixa a imagem final bem menor, o que importa para o tempo
# de deploy e de cold start no plano gratuito.
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
