## Spring Boot Web Apps with Redis, MongoDB and JPA

**New** Responsive bootstrap HTML5 templates. Now works on mobile devices, tablets... etc.

![Contacts Application](images/redis-phone.jpg?raw=true "Responsive Contacts Application")

1.  **SpringBoot and JPA** [springboot-rest-jpa-contacts](springboot-rest-jpa-contacts "JPA Web Application") - Responsive JPA with MySql or embedded H2. A `docker` ready project with SpringBoot 2.7.2 and JDK 19.

2.  **SpringBoot and MongoDB** [springboot-rest-mongo-contacts](springboot-rest-mongo-contacts "MongoDB Web Application") - Responsive - live running `MongoDB` server or embedded `de.flapdoodle`. A `docker` ready project with SpringBoot 2.7.2 and JDK 19.

3.  **SpringBoot and Redis as an RDBMS** [springboot-rest-redis-contacts](springboot-rest-redis-contacts "Redis Web Application") - Responsive - Redis as an RDBMS with Redis server 6.2 or `it.ozimov` embedded. A `docker` ready project with SpringBoot 2.7.2 and JDK 19.

4.  **SpringBoot send and recieve Google Protobuf messages** [springboot-js-protobuf-example](springboot-js-protobuf-example) is a SpringBoot server / JavaScript client (no node.js) protobuf messages consuming and producing example project. **no node.js** This project uses vanilla `java script and jquery`.

5. **SpringBoot Maven Web Project Template** [template-web-project](template-web-project) is a Spring Boot template starter web project used by all projects in this repository.

6. **NO LONGER MAINTAINED** [springboot-rest-scala-jpa-contacts](springboot-rest-scala-jpa-contacts "Scala JPA Web Application") - Scala JPA with MySql or embedded H2. SpringBoot 2.5.3 and JDK 16.

7. **TODO** Separate the layers into microservices, run them in a service
registry and manage it all through `Kubernetes`, then have it ready to deploy on IBM Cloud,
Cloud Foundry, Amazon... etc.


## Table of contents

* [What's in this repository?](#what-s-in-this-repository)
* [Executive Summary - Running the Application](#executive-summary-running-the-application)
* [What is demonstrated here?](#what-is-demonstrated-here)
     * [Spring Security](#spring-security)
     * [Web MVC](#web-mvc)
     * [Web MVC](#web-mvc)
     * [Spring REST](#spring-rest)
     * [Spring Data](#spring-data)
     * [Spring AOP](#spring-aop)
     * [Javascript Client](#javascript-client)
     * [Testing](#testing)
     * [Dockerizing a Spring Boot Web Application](#dockerizing-a-spring-boot-web-application)
* [System Requirements](#system-requirements)
* [Login and Authentication Credentials](#login-and-authentication-credentials)
* [Testing and running the applications from a shell window](#testing-and-running-the-applications-from-a-shell-window)
* [Running with embedded persistence storage for minimal installations](#running-with-embedded-persistence-storage-for-minimal-installations)
* [Testing with embedded persistence storage](#testing-with-embedded-persistence-storage)
* [Running with an installed persistence storage like MySQL or MongoDB](#running-with-an-installed-persistence-storage-like-mysql-or-mongodb)
* [Initial MySQL database setup prior to running the application](#initial-mysql-database-setup-prior-to-running-the-application)
* [Initial MongoDB instance setup prior to running the application](#initial-mongodb-instance-setup-prior-to-running-the-application)
* [Running with installed persistence storage](#running-with-installed-persistence-storage)
* [Running and testing the applications from IDE](#running-and-testing-the-applications-from-ide)
* [Eclipse IDE setup](#eclipse-ide-setup)
* [Running from Eclipse](#running-from-eclipse)
* [JetBrains IntelliJ IDEA setup](#jetbrains-intellij-idea-setup)
* [Running from JetBrains IntelliJ IDEA](#running-from-jetbrains-intellij-idea)
* [Additional Info](#additional-info)
* [Credits](#credits)
* [License](#license)


![Contacts Application](images/contacts.jpg?raw=true "Contacts Application")


## What's in this repository?


* Answers to a million stack overflow questions! How to integrate embedded servers into spring boot applications and
activate them based on maven profiles, how to serve both JSP and template content together (from a fat jar no less) and
how to query different persistence storages using Spring SpEL even when the storage doesn't support a query language
and many (too many) more answers are waiting here for you to discover.


* Spring-boot web application projects displaying master/detail html pages, allowing
the user to search, view, edit and add contacts (people details) to a persistence storage. Basically,
these are a CRUD applications built upon a REST API. All CRUD operations are performed
by a JQuery based java-script client. The difference between those samples, is the perssistence
storage used. These samples demonstrate using sql and no-sql technologies and even using `scala`
to do so.


- **Now Dockerized** with an extensive guide on 'how to' - the **JPA** based project is
[springboot-rest-jpa-contacts](springboot-rest-jpa-contacts "JPA Web Application"). Depending on the active profile,
this application can run with an embedded instance of H2 database, or using a production instance of MySQL. This
project demonstrates using **JPA** (MySql 8 or an embedded instance of H2) as the persistence storage for a Spring
Boot RESTfull API web application.



- The **MongoDB** based project is [springboot-rest-mongo-contacts](springboot-rest-mongo-contacts "MongoDB Web Application").
Depending on the active profile, this application can run with an embedded instance of MongoDB,
or using a production instance of MongoDB. This project demonstrates using **MongoDB** as the persistence storage for a
Spring Boot RESTfull API web application.


- **Now Dockerized** with an extensive guide on 'how to' - the **Redis** based project is
[springboot-rest-redis-contacts](springboot-rest-redis-contacts "Redis Web Application"). Depending on the active
profile, this application can run with an embedded Redis instance or a production grade single stand-alone instance of
Redis (tested against Redis Server 6.2.4). This project demonstrated **Using Redis as an RDBMS** and solving the
myriad of issues that come with this territory with Spring Boot. This is really not easy! Don't use Redis as an RDBMS
on production. It will work and work nicely and fast, but development involves a lot of overhead - even with Spring
Boot. Besides... it's not what redis is for. But... If you insist, it's feasible and I've solved many of the issues
and annoyances dictated by such a choice.


- **NO LONGER MAINTAINED** The **Scala**/**JPA** project is
[springboot-rest-scala-jpa-contacts](springboot-rest-scala-jpa-contacts "Scala JPA Web Application").
Depending on the active profile, this application can run with an embedded instance of H2 database, or using a
production instance of MySQL. This project demonstrates... well, nothing really other than using a dead programming
language (I guess). Other than using **Scala** in this project, it's nearly identical to the Java based **JPA**
project in the first bullet.


## Executive Summary - Running the Application:


*  make sure you have [JDK 18](https://jdk.java.net/18/ "JDK 18.0.1.1") installed!

*  if you don't have [Apache Maven](https://maven.apache.org/ "Apache Maven") installed, you can use the wrapper.

*  I have installed the wrapper in each of the projects using the command:

> mvn -N io.takari:maven:0.7.7:wrapper -Dmaven=3.8.1


##### Basic commands to run and test the application


* `cd` into one of the projects from a command line shell


* type: `./mvnw clean package spring-boot:run -DskipTests` to run the application


* or type: `./mvnw clean test` to test the application


* or type: `./mvnw clean test -Dtest=ApplicationIT -Ptest` to run the application in `test` profile (runs for 5 minutes and exits)


* browse [http://localhost:8080](http://localhost:8080 "http://localhost:8080") to use the application


* see [Testing and running the applications from a shell window](#testing-and-running-the-applications-from-a-shell-window) for details


##### Don't want to delve in the bits and bytes? That's fine! All you got to do is:


* clone this repository


* configure $JAVA_HOME (%JAVA_HOME% on Windows) env. variable to point to a JDK 16 installation


* `cd` into one of the projects from a command line shell


* run: `./mvnw clean package spring-boot:run -DskipTests`


* goto [http://localhost:8080](http://localhost:8080 "http://localhost:8080")


* Have fun!


## What is demonstrated here?


These applications demonstrate many aspects of the [Spring Framework](https://spring.io/projects/spring-framework "Spring Framework")  and the [Spring Boot](https://spring.io/projects/spring-boot "Spring Boot") Ecosystem. There's too many to list, but the essence is:


#### Spring Security

*   configuring web security based on active profile
*   authorizing different requests with ant matchers
*   configuring login form, basic authentication and logout with clearing the session
*   enabling / disabling csrf protection

#### Web MVC

*   using old JSP templating together with modern Thymeleaf by resolving views
*   adding resource handlers to handle serving of static resources
*   adding interceptors to respond to locale and theme changes
*   using CSS themes
*   adding automatic view controllers to serve content
*   adding argument resolvers to support custom controller method argument types.
*   resolving errors
*   supporting i18n multiple languages

#### Spring REST

*   adding custom REST validators
*   adding custom REST converters
*   configuring REST repositories
*   creating a RESTful API
*   supporting paging and sorting

#### Spring DATA

*   Working with MongoDB, generating sequences, defining documents, collections security
    and repository populators
*   Working with an embedded MongoDB
*   Working with JPA and MySQL, defining entities and entity listeners
*   Working with an embedded H2 database
*   Using Flyway to manage DDL and DML scripts with JPA
*   Using Spring SpEL to create custom complex @Query annotations (substitute for Criteria API)
*   Configuring automatic auditing
*   Exporting / Hiding repository methods to REST API
*   Implementing paging and sorting

#### Spring AOP

*   Configuring a custom annotation to signal method auditing
*   Configuring a custom aspect to perform auditing on delete (substitutes Hibernate Envers)

#### JavaScript Client

*   Performing asynchronous AJAX request with JQuery
*   Setting up AJAX with JQuery
*   Dynamically building an HTML div from json response via JQuery
*   Performing search, filtering, paging, sorting using AJAX with JQuery

#### Testing

*   Profile based configurations for Mongo, JPA and Rest tests
*   Extending a JUnit 5 base test to globally use @BeforeAll and @BeforeEach
*   Using @ParameterizedTest and @CsvSource to run the same test. many times with
    different test arguments
*   Using a RestTemplate and TestRestTemplate to run REST operations against a RESTful API
*   Using a MockMVC to run REST operations against a RESTful API
*   Using Surefire and Jacoco maven plugin for testing and coverage


#### Dockerizing a Spring Boot Web Application

*   see [springboot-rest-redis-contacts](springboot-rest-redis-contacts "Redis Web Application") for the extensive
'how to'.


## System Requirements

These applications were designed for minimum installations. They are built
with [Apache Maven](https://maven.apache.org/ "Welcome to Apache Maven"). The projects
include a [maven wrapper](https://github.com/takari/maven-wrapper "maven wrapper"), so if
you don't have a Maven installation, you can use the wrapper, but:


* **You will need to install [JDK 16](https://jdk.java.net/16/ "JDK 16.0.1")** or higher.


Besides that, all you basically need is:


- To run/test the applications without editing code in an IDE (e.g Eclipse, IDEA, etc...):

> *   Java 16 (or higher) - available from [jdk.java.net](https://jdk.java.net/16/ "JDK 16.0.1")

- To run the applications from an IDE: (These projects were developed using
  [Eclipse IDE for Enterprise Java and Web Developers](https://www.eclipse.org/downloads/packages/ "Eclipse IDE 2021-06 R"))

> *   Java 16 (or higher) - available from [jdk.java.net](https://jdk.java.net/16/ "JDK 16.0.1")
> *   A Java 16 enabled IDE configured in accordance with the JPMS restrictions.
> *   You will need to add the following configurations to your IDE's init script (i.e. `eclipse.ini`) or as JVM arguments

>     --add-modules=ALL-SYSTEM
>     --add-opens=java.base/java.lang=ALL-UNNAMED


## Login and Authentication Credentials

The default **credentials** for performing DML operations like editing or deleting a contact are `user/password`:

>  username: 'user'

>  password: 'password'

These credentials are enforced by Spring Security

The same credentials, however, are used to authenticate against the persistence storage where
relevant (i.e installations of MongoDB and MySQL). These are enforced by the persistence storage.

For convenience, both mechanisms use the same credentials.


![Login](images/login.jpg?raw=true "Login")


## Testing and running the applications from a shell window

You can run/test the application from command line using your own maven installation or
using the [maven wrapper](https://github.com/takari/maven-wrapper "maven wrapper") supplied
in each project.

-  In general - to run a maven command navigate to the project's root installation directory:

> *   user@localhost:/workspaces/springboot/springboot-rest-jpa-contacts$ `mvn <command>`

- For example:


> *   user@localhost:/workspaces/springboot/springboot-rest-jpa-contacts$ `mvn clean test`


-  If you do not have maven installed, run with the supplied maven wrapper. Like before, navigate to the project's root installation directory:

> *   user@localhost:/workspaces/springboot/springboot-rest-jpa-contacts$ `./mvnw <command>`


- For example:


> *   user@localhost:/workspaces/springboot/springboot-rest-jpa-contacts$ `./mvnw clean test`


* The maven wrapper is installed with the following command:


> * mvn -N io.takari:maven:0.7.7:wrapper -Dmaven=3.8.1


Before you begin running tests, the [jacoco maven plugin](https://www.eclemma.org/jacoco/trunk/doc/maven.html "jacoco maven plugin")
coverage plugin might need to install the instrumentation agent. If you do not see the jacoco maven
plugin reporting the outcome of the tests coverage, or if you see a warning about a missing execution
file:

-  it maybe required only once with a fresh installation (all maven commands **executed from the project's root directory**):

> *   `./mvnw jacoco:prepare-agent`

- or

> *   `mvn jacoco:prepare-agent`


### Running with embedded persistence storage for minimal installations

You can run or test the application with an embedded persistent storage or run it with your
own installed instance of MongoDB or MySQL. Unit and integration tests, however, always run
against an embedded instance. This depends on the spring active profile (`test` or
default `dev` profile), which is defined in the `pom.xml'. However, running the application
from command line can only be done in `dev` or `prod` profiles.

-  To run in embedded persistent storage mode - and skip testing:

> mvn clean package spring-boot:run -Pdev -DskipTests

-  Here, the -P flag stands for `profile` and `dev` is the value of the flag. When you run the application
in command line like this, the last few messages report the success of the run and should be something like:

> `o.s.b.w.e.tomcat.TomcatWebServer.start -> Tomcat started on port(s): 8080 (http) with context path ''`

> `com.tnsilver.contacts.Application.logStarted -> Started Application in 4.994 seconds (JVM running for 5.429)`

-  You can now use a browser to navigate to [http://localhost:8080](http://localhost:8080 "http://localhost:8080") to review the application.


- You can also run the application in `test` profile within the framework of JUnit testing by issuing the following command:


> mvn clean test -Dtest=ApplicationIT


- The above command runs the `ApplicationIT` integration test, which is excluded from all other integration tests.
Note this special test will run the application for **10 minutes** and exit. While the test is running, you can navigate
to [http://localhost:8080](http://localhost:8080 "http://localhost:8080") to use the application through the browser.
You can also issue curl commands against the REST API of the application.
At any before the 10 minutes run expires, you can stop the application with **CTRL+C**.


### Testing with embedded persistence storage

All unit tests run with profile `test` which automatically loads an embedded persistence storage.

-   All you have to do is:

> mvn clean test

*   The test reports are generated to `target/site/surefire-report.html`


*   The coverage reports are generated to `target/site/jacoco/index.html`


You can run all tests, including integration tests, with:


> mvn clean verify


*   The failsafe reports are generated to `target/site/failsafe-report.html`



### Running with an installed persistence storage like MySQL or MongoDB

You can run the application with an installed instance of a persistent storage.
This application was developed with:

*   **MySQL Server** version: `8.0.25-0ubuntu0.20.04.1 (Ubuntu)`


* see [How to Install MySQL on Ubuntu 20.04](https://phoenixnap.com/kb/install-mysql-ubuntu-20-04 "How to Install MySQL on Ubuntu 20.04") or [How to install MySQL database server 8.0.19 on Windows 10](https://www.sqlshack.com/how-to-install-mysql-database-server-8-0-19-on-windows-10/ "How to install MySQL database server 8.0.19 on Windows 10"),
or refer to  [https://dev.mysql.com/downloads/installer/](https://dev.mysql.com/downloads/installer/ "MySQL Community Docs")  for the specific installation on your operating system.

*   **MongoDB Server** version: `4.4.6`

*   see [https://docs.mongodb.com/manual/installation/](https://docs.mongodb.com/manual/installation/ "Install MongoDB")  for the installation of MongoDB


#### Initial MySQL database setup prior to running the application

* log in to the mysql shell with an administrative user (i.e. `roor`) and redirect
  the `src/once-as-root.sql` script to it

> mysql -u root < sql/once-as-root.sql

* Alternatively, you can login to the mysql shell using an administrative user (i.e. `roor`)
  and just spill the contents of the `once-as-root.sql' script to the console.

> mysql -u root

> copy and paste the script content into the shell

* You can also login to the mysql shell using an administrative user (i.e. `roor`) and use
  the `source` command with a **full path** to the `once-as-root.sql` script.

> mysql -u root

> source /workspaces/jee-2021-06/springboot-mongo-jpa-webapp/springboot-rest-jpa-contacts/sql/once-as-root.sql


#### Initial MongoDB instance setup prior to running the application

You'll have to create a user for database authentication.  This is done by logging-in to
the mongo shell, and creating the `contacts` database plus the administration user that
the application uses.

* start the mongo shell from command line (with newer versions of MongoDB, this is now called `mongosh`):

>     `mongo`

* Execute the following commands:

>     use contacts

>     db.createUser({
>     user: "user",
>     pwd: "password",
>     customData: { "name": "user", "roles": "readWrite, dbAdmin" },
>     roles: [ { role: "readWrite", db: "contacts" },
>              { role: "dbAdmin", db: "contacts" } ]
>     })

*  from now on the local MongoDB instance connection string will be:

>     mongodb://user:password@localhost:27017/?authSource=contacts&readPreference=primary&appname=MongoDB%20Compass&ssl=false


#### Running with installed persistence storage

To run with installed instance of a persistence storage, **you first need to install one**.
If you don't install an instance of a persistence storage, the application won't run and will
output a load of error messages and stack traces. After the installation and one time setup,
just specify the `prod` profile in the maven command and skip the tests:

> mvn clean package spring-boot:run -Pprod -DskipTests

When you run the application in command line like this, Spring Boot reports the profile in
one of the first messages after the text splash:

> `com.tnsilver.contacts.Application.logStartupProfileInfo -> The following profiles are active: prod`

Like before, this runs the application, but this time against an installed instance of the persistence storage.
You can again use a browser to navigate to [http://localhost:8080](http://localhost:8080 "http://localhost:8080")
to review the application.


## Running and testing the applications from IDE

You can also run the Java applications from inside your IDE.
This section details the how-to for Eclipse IDE and for JetBrains IntelliJ IDEA.

**Note** Scala IDE plugin for Eclipse is no longer supported. It will not run with Java 16 (it's most recent supported version is Java 11, and more commonly Java 1.8). I could not run the Scala application from Eclipse. For every time I've
installed the Scala IDE from Eclipse Market place or through the update site, it totally screwed up my `jee-2021-06-R` Eclipse and it started to yield a load of unstoppable error messages.

I had to work magic to get it running in IntelliJ IDEA, and by some tedious configurations and by adding a scala library as a dependency to the project, I've finally done it. That wasn't a fun either. Half the normal features didn't work, including synchronization of the editor with the project tree and the build took relatively long. I guess Scala isn't the most vibrant programming language by 2021. With IDEA, however, I could run the application and unit tests.


### Eclipse IDE setup

These applications were developed with [Eclipse IDE 2021-06-R for JavaEE and Web Developers](https://www.eclipse.org/downloads/packages/ "Eclipse IDE 2021-06 R").
The IDE runs with [JDK 18.0.1.1](https://jdk.java.net/18/ "JDK 18.0.1.1") JRE.  There's a few
configurations to take in mind before importing any of the projects into eclipse.

Configure lombok instrumentation. [Project Lombok](https://projectlombok.org/ "Project Lombok") is a
java library that automatically plugs into your editor and builds getters, setters, constructors,
equals and hashcode methods, a toString method and even logging by declaring annotations,
without actually having to write code. While Maven resolves lombok instrumentation via the
dependency, Eclipse needs to be explicitly aware of it.


* download lombok from [https://projectlombok.org/download](https://projectlombok.org/download "lombok jar")

>  run `java -jar lombok.jar`

* follow the installer stages. At the end of it, `lombok.jar` will end up in the eclipse installation directory, and the following line will be added to the eclipse configuration file `eclipse.ini`:

>  -javaagent:/opt/java/jee-2021-06-M3/lombok.jar

* You will also need to add the following JPMS restriction canceling configurations
  to `eclipse.ini`. You can add this after or before the `-javaagent...` line that lombok
  added to the file:

>     --add-modules=ALL-SYSTEM
>     --add-opens=java.base/java.lang=ALL-UNNAMED

* Restart Eclipse and use the `file -> import -> Maven -> Exisiting Maven Projects` menu,
  and navigate to the root directory of one of the maven projects (not this wrapper directory)
  and choose the desired `pom.xml` file. Eclipse will load the project to the project explorer.


* **Note** that after the import, Eclipse will start downloading the required maven dependencies.
  This can take some time. Allow Eclipse to finish the process before trying to run the application
  or editing and saving any of the code files.

* Once Eclipse builds the project, navigate to the `Help -> Eclipse Marketplace` menu. When the
  menu loads, filter for 'spring' (using the 'Find:' box) and install `Spring Tools 4` (formerly
  known as Spring Tool Suit 4). Restart the IDE after the plugin is installed.

### Running from Eclipse

* You can run the `com.tnsilver.contacts.Application` as a `Spring Boot App`, or
  the `com.tnsilver.contacts.ApplicationIT` as a `Java Application`. In either case,
  right click on the respective file in the project explorer and select the `Run As` sub menu.


* `com.tnsilver.contacts.Application` runs by default in `dev` profile and uses an embedded
  persistence storage. It can also run in `prod` profile using an installed instance of a
  persistence storage.


* You can activate the `prod` profile by right clicking on the project node in the project
  explorer and choosing `Maven -> Select Maven Profiles` from the sub menu. Then activate
  the `prod` profile.


* After a profile change - a rebuild is required, so that maven processes the resources based
  on the new active profile. Select `Project - Clean` from the main menu and make sure
  the `Build Automatically` checkbox is ticked.


* Finally run the application as before using  `Run As -> Spring Boot App`.


* `com.tnsilver.contacts.ApplicationIT` runs **only** as a Java Application in `dev` profile
  and uses an embedded persistence storage. It cannot be run in other profiles. You can run it
  only using `Run As -> Java Application` from the right click sub menu. It is used to
  test `curl` command vs, the REST API without security restrictions of [`csrf`](https://en.wikipedia.org/wiki/Cross-site_request_forgery "CSRF")
  protection.

### JetBrains IntelliJ IDEA setup

*  If you are using JetBrains IntelliJ IDEA - just follow the steps
   in [Lombok IntelliJ IDEA](https://projectlombok.org/setup/intellij "IntelliJ IDEA") to add
   lombok support to your IDE.


*  Load any of the projects into IntelliJ by selecting the `pom.xml` file.


*  Choose to open it as 'Maven project' - if IDEA recognizes more than one project
   configurations, and if the dialog is opened.


*  Confirm the `Trust this project` if the dialog asks.


*  Configure a Java 16 SDK by using the `File -> Project Structure` main menu and select
   a Java 16 SDK, or add one if it's missing.


*  Make sure to use the `Java 16 - Records, patterns, local enums and interfaces` in
   the `Project language level`


### Running from JetBrains IntelliJ IDEA


*  Run the any of the `Application` or `ApplicationIT` by right clicking
   and choosing `Run` from the menu, or by opening the file in the editor
   and clicking the green triangle shortcut icon from the top menu.


## Additional Info


* Refer to the [README.md](springboot-rest-jpa-contacts "README.md") of
  the `springboot-rest-jpa-contacts` project for details about
  how to `curl` to the REST API.


* Refer to the [README.md](springboot-rest-mongo-contacts "README.md") of
  the `springboot-rest-mongo-contacts` project for details about
  how to `curl` to the REST API.


* Refer to the [README.md](springboot-rest-redis-contacts "README.md") of
  the `springboot-rest-redis-contacts` project for details about
  how to `curl` to the REST API and about `Dockerizing the Application`.


## Credits


* Many thousands of developers from Sun Microsystems, Oracle, IBM, Microsoft, Google, Netflix, Amazon, and people in the Apache
foundation, Spring IO, Eclipse, IntelliJ, Maven plugin developers, the JQuery team and many others - all deserve credit for
the open source libraries I use.


* Special thanks to [Geoff Hudik](https://geoffhudik.com/tech/author/thnk2win/ "Geoff Hudik") for the [Docker pass Credential Helper on Ubuntu](https://geoffhudik.com/tech/2020/09/15/docker-pass-credential-helper-on-ubuntu/ "Docker pass Credential Helper on Ubuntu")


* and...


![T.N.Silverman](images/me.jpg?raw=true "T.N.Silverman")

* All java code, property files, client Java Scripts, JSP's and HTML templates created by [T.N. Silverman](https://github.com/tnsilver "About T.N.Silverman")


## License


[Licensed under the Apache License, Version 2.0](LICENSE.md "Apache License")

