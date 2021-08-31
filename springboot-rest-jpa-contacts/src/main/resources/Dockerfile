##############################################
##                                          ##
## pom packaging is 'war' by default. We    ##
## cannot server JSP's with 'jar' packaging ##
##                                          ##
##############################################
FROM openjdk:16.0.1-jdk-slim
EXPOSE 8080
ADD target/jpa-contacts.war jpa-contacts.war
ENTRYPOINT ["java","-jar","jpa-contacts.war"]
##############################################
##                                          ##
## If we change the pom packaging to 'jar': ##
##                                          ##
##############################################
# First Part: Extract the layered jars
#FROM openjdk:16.0.1-jdk-slim as builder
#WORKDIR application
#ARG JAR_FILE=target/*.jar
#COPY ${JAR_FILE} application.jar
#RUN java -Djarmode=layertools -jar application.jar extract
# Next Part: Run the app
#FROM openjdk:16.0.1-jdk-slim
#WORKDIR application
#COPY --from=builder application/dependencies/ ./
#COPY --from=builder application/snapshot-dependencies/ ./
#COPY --from=builder application/spring-boot-loader/ ./
#COPY --from=builder application/application/ ./
#ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
