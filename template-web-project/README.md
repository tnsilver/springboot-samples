Template Web Project
====================

## Executive: running the app (embedded Tomcat)

* `cd template-web-project`

* `mvn clean package spring-boot:run -DskipTests=true`

* Navigate to (http://localhost:8080)

## Executive: Deploying on external Tomcat 9

* `cd template-web-project`

* `mvn clean package -DskipTests`

* `cp target/template-web-project.war $CATALINA_HOME/webapps`

* `. $CATALINA_HOME/bin/catalina.sh start`

*  Navigate to (http://localhost:8080/template-web-project)


## To install on eclipse IDE

* make sure you have [lombok 1.18.24](https://projectlombok.org/download) in eclipse installation directory (along side with `eclipse.ini` file).

* add the entry `-javaagent:/<path-to-eclipse-ide>/eclipse-jee-2022-06-R/lombok.jar` at the end of the `eclipse.ini` file.

* use the `File -> import -> Maven -> existing maven projects` menu and navigate to the root of this directory.

* make sure you see the `pom.xml` file in the import wizard and confirm.

* allow sufficient time for maven plugin to download all the project dependencies

