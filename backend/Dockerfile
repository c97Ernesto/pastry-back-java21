# ================================================
# Dockerfile para Render - Malva Pastry Shop
# Basado en la documentación oficial de Render
# https://docs.render.com/docker
# ================================================

# ================== BUILD STAGE ==================
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copiar Maven wrapper y pom.xml primero (para cache de dependencias)
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Dar permisos de ejecución al Maven Wrapper
RUN chmod +x mvnw

# Descargar dependencias (cacheado si pom.xml no cambia)
RUN ./mvnw dependency:go-offline -B

# Copiar código fuente
COPY src/ src/

# Compilar aplicación
RUN ./mvnw clean package -DskipTests -B

# ================== RUN STAGE ==================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copiar JAR desde etapa de build
COPY --from=builder /app/target/*.jar app.jar

# Usuario no-root por seguridad
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Variables de entorno para JVM
ENV JAVA_OPTS="-Xmx256m -Xms128m"

# Puerto expuesto (Render usa $PORT, default 10000)
EXPOSE 10000

# Comando de inicio con perfil de producción
# Render inyecta PORT automáticamente
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-10000} -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod} -jar app.jar"]
