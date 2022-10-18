# Contacts: Spring Boot RESTful CRUD Web-App with JPA

New: Responsive bootstrap html and JSP templates. Now works on phones and tablets.

### A Docker Ready Spring Boot Demo with MySQL and H2

## What's in here?

A Docker ready SpringBoot CRUD (create, retrieve, update delete) RESTful (with a
JavaScript client) web application with a master/details UI to manage a list of
contacts with **JPA** and `MySQL` or `H2` serving as the persistence storage. Here
you can see how JSP's and Thymeleaf templates reside together and are being
served from a fat spring boot jar (war actually) and how SpEL is used to perform
elaborate custom SQL queries. This README file will also walk you through the
basic to advanced methods of dockerizing the application.


## Table of contents


* [Getting Started](#getting-started)
* [Initial MySQL Setup](#initial-mysql-setup)
* [Installing, testing and running the application](#installing-testing-and-running)
* [Executive Summary: Running the Application](#executive-summary)
* [Testing the REST API with curl](#testing-the-rest-api-with-curl)
* [Exporting the host address to a local environment variable](#exporting-the-host-address-to-a-local-environment-variable)
* [Performing REST queries with curl](#performing-rest-queries-with-curl)
* [Performing data manipulation with curl](#performing-data-manipulation-with-curl)
* [Containerizing the Application with Docker](#containerizing-the-application-with-docker)
    * [Method 1: package and docker compose](#method-1-package-and-docker-compose)
    * [Method 2: Spring Boot Buildpacks](#method-2-spring-boot-buildpacks)
    * [Method 3: Spring Boot Layered Jars](#method-3-spring-boot-layered-jars)
    * [Method 4: Containerizing with JIB](#method-4-containerizing-with-jib)
    * [Method 5: Pushing and pulling an image with JIB](#method-5-pushing-and-pulling-an-image-with-jib)
* [Credits](#credits)
* [License](#license)


![Contacts List Redis](../images/jpa/contacts-jpa.jpg?raw=true "JPA Contacts")


![Add Redis Contact](../images/jpa/contacts-jpa-add.jpg?raw=true "Add JPA Contact")


![About Redis Contact](../images/jpa/contacts-jpa-about.jpg?raw=true "About JPA Contact")


## Getting Started


* `cd` into the project's root directory from a command line shell


* type: `./mvnw clean package spring-boot:run -DskipTests` to run the application


* or type: `./mvnw clean test` to test the application


* or: `./mvnw clean test -Dtest=ApplicationIT -Ptest` to run the application in `test`
profile (runs for 5 minutes and exits)


* go to [http://localhost:8080](http://localhost:8080 "http://localhost:8080")
to use the application


* You can login to the embedded H2 database console at [http://localhost:8080/h2-console](http://localhost:8080/h2-console "http://localhost:8080/h2-console") using the database connections string `jdbc:h2:mem:CONTACTS;DB_CLOSE_DELAY=-1` with credentials of the standard h2 username `sa` and empty password.


* see [Testing and running the applications from a shell window](../README.md#testing-and-running-the-applications-from-a-shell-window) for details


## Initial MySQL Setup


* Before you can run the application against a pre-installed MySQL 8 server,
you have to create the database and user that the application uses. You'll also
have to grant the user permissions (roles) on the tables in the created
database.


* The `sql/once-as-root.sql` script is designed to perform the tasks needed to
initialize the MySQL 8 database.


* Run the script as a MySQL `root` user:


>    * mysql -u root < sql/once-as-root.sql

* If you're having trouble logging to mysql as a non-adminitrative linux user,
fix it by logging in to mysql as linux root with `sudo` and then running the
following commands in the mysql shell:

>
```
DROP USER 'root'@'localhost';
CREATE USER 'root'@'localhost' IDENTIFIED BY '';
GRANT ALL PRIVILEGES ON *.* TO 'root'@'localhost' WITH GRANT OPTION;
FLUSH PRIVILEGES;
```

* After you logout from the mysql shell you'd be able to login to mysql with a
non administrative linux user.


## Installing, testing and running


*  See the [parent README](../README.md "parent README.md") file for
installation details.


*  The `curl` commands in this README file end with piping the `jq .` command.
JQ is a `json` processor, which allows for pretty printing the json responses.


* Follow the [official jq installation guide](https://stedolan.github.io/jq/download/ "jq installation") to install
JQ for your operating system.


## Executive Summary

* from the root directory of this project:


* run: `./mvnw clean package spring-boot:run -Pdev -DskipTests`


* goto [http://localhost:8080](http://localhost:8080 "http://localhost:8080")


* Have fun!


## Testing the REST API with curl


This _RESTful_ application uses a **REST API** to manage contacts. A JavaScript
client performs the REST calls to the API via JQuery ajax'ing. To catch a
glimpse of this _RESTful_ API without the client scripts, for testing purposes,
you can issue `curl` commands to the API end points:

* Start by running the application in `dev` profile from command line:

>    * mvn clean package spring-boot:run -Pdev -DskipTests


## Exporting the host address to a local environment variable

* In a new shell (command line) window, export the host URL to ease the **curl**
command usage:

>    * export HOST=localhost:8080


## Performing REST queries with curl


* then you can start to issue `curl` commands, for examle, to test the
functionality of the `HelloRestController`, which is a simple REST controller that
does not involve any persistence operations. It basically just greets the user:


* To say hello to 'Homer':

>    * curl -v -H "Content-Type: application/json" -X GET $HOST/api/hello/Homer | jq .


* To say hello to 'Homer' 'Simpson':

>    * curl -v -H "Content-Type: application/json" -X GET  $HOST/api/hello/Homer/Simpason | jq .


* Now, query the contacts profile provided by ALPS. Notice the `application/alps+json`
response type. This exposes the available public REST API:

>    * curl -v -G $HOST/api/profile/contacts | jq .


* next, query the paged contacts. Notice the `application/hal+json` response type
and the paged response:


>    * curl -v  -G -k "$HOST/api/contacts?size=100" | jq .


* You can use the search API with the `/api/contacts/search/byParams` end point to
retrieve both parents of the Simpson family:

>
```
curl -X "GET" -v -H "Content-Type: application/json" \
-k "$HOST/api/contacts/search/byParams?ssn=&firstName=&lastName=Simp&birthDate=&married=true&children=3" | jq .
```

* The same result can be achieved with:

>
```
curl -X "GET" -v -H "Content-Type: application/json" \
-k "$HOST/api/contacts/search/byParams?lastName=Simp&married=true" | jq .
```


* Notice the **pagination parameters** at the end of the `curl` URL, and the
pagination data at the end of the `application/hal+json` response:

>
```
curl -X "GET" -v -H "Content-Type: application/json" \
-k "$HOST/api/contacts/search/byParams?lastName=Simp&married=&page=0&size=5&sort=id,ASC" | jq .
```

* This is a useful search method so we can use a more complex filter to find
only the children of the `Simpson` family:

>
```
curl -X "GET" -v -H "Content-Type: application/json" \
-k "$HOST/api/contacts/search/byParams?lastName=Si&children=0" | jq .
```

* It also supports dates, so we can find `Homer Simpson` by a `birthDate` parameter:

>
```
curl -X "GET" -v -H "Content-Type: application/json" \
-k "$HOST/api/contacts/search/byParams?birthDate=1978-06-20" | jq .
```

* Spring Boot will provide a default unsorted page (size 10) in this case where
we don't specify any pagination criteria. It is a very powerful method. In fact,
this exact same method `byParams` is used by the java script client in the
contacts list master page in the application. Users can filter the criteria to
find and choose the sorting property, direction and page size, and JQuery just
collects the parameters and uses ajax to call the REST API of the application
very similarly to the previous `curl` commands.


## Performing data manipulation with curl


Since Spring Security is authorizing requests and does not allow anonymous
access to data manipulation operations via the appropriate `POST`, `PUT`, `PATCH`
or `DELETE` REST `http methods`, from now on all `curl` commands must include the
 `-u user:password` basic authentication data.


* The following curl commands require disabling `csrf protection` in `SecurityConfig`
which it does for the `test` and `dev` profiles.


* We can **INSERT** (create or save) a new contact with `curl` and the `POST`
http method. Notice the json (`-d`) data format and the http headers, each with
the `-H` flag. The response status is `201` (created) and the response
Content-Type is `application/hal+json`.

>
```
curl  -v -u user:password -X POST $HOST/api/contacts \
-H "Content-Type: application/json" \
-d '{"ssn":{"ssn":"987-56-4231"},
  "firstName":"Lucky",
  "lastName":"Luciano",
  "birthDate":"1897-11-24",
  "married": true,
  "children":"0"}' | jq .
```

* We can **UPDATE** a new contact with `curl` and the `PUT` http method.
The response status is `200` (OK) and the response Content-Type is
`application/hal+json`.

>
```
curl  -v -u user:password -X PUT $HOST/api/contacts/35 \
-H "Content-Type: application/json" \
-d '{"id": 35,
  "ssn":{"ssn":"987-56-4231"},
  "firstName":"Lucky",
  "lastName":"Luke",
  "birthDate":"1946-11-24",
  "married": false,
  "children":"0"}' | jq .
```


* We can also **UPDATE** just the changes applied to an existing contact with
`curl` and the `PATCH` http method. Here we can supply only the delta of changes
(last name and birth date in this case). The response status is `200` (OK) and
the response Content-Type is `application/hal+json`.

>
```
curl  -v -u user:password -X PATCH $HOST/api/contacts/35 \
-H "Content-Type: application/json" \
-d '{"firstName":"Test","lastName":"Tester", "birthDate": "1897-11-24"}' | jq .
```

* Finally... we don't know who that contact is anymore, so we can delete them
with `curl` and the `DELETE` http method. There's no response body after deletion,
but the response status is `204` (deleted)

>
```
curl -v -u user:password -X DELETE "$HOST/api/contacts/35"
```

* if we try that again, the response status is `404` (not found), which is
expected as we've just deleted the contact.

>
```
curl -v -u user:password -X DELETE "$HOST/api/contacts/35"
```

* If we need to restore the deleted contact, we use `curl` to `POST` it again.
Notice the id of the contact is now different than before. Again, the response
status is `201` (created) and the response Content-Type is `application/hal+json`.

>
```
curl  -v -u user:password -X POST $HOST/api/contacts \
-H "Content-Type: application/json" \
-H "Cache-Control: no-cache" \
-d \
'{"ssn":{"ssn":"987-56-4231"},
  "firstName":"Lucky",
  "lastName":"Luciano",
  "birthDate":"1897-11-24",
  "married": true,
  "children":"0"}' | jq .
```

* We can query all the contacts and see the new id. Notice the gap between the
next to last contact (`id`: 34) and our new contact (`id`: 36). The gap was
created when we initially deleted the contact. The identity property of the
contacts is unique and non-restoreable in this application.

>
```
curl -v  -k "$HOST/api/contacts?size=100" | jq .
```

## Containerizing the Application with Docker


* The next section talks about 5 methods to create an OCI image and deploy the
application to docker. These are not the only ways to achieve the same goal, but
they sum up the basics. The `Clean-Up` routine in each section is specific to the
described method, but at any time you can run the bash script `dkclean.sh` from
the root directory of the project. The script will clean up docker and remove
the images and containers added with each method used.


* The `dkclean.sh` accepts the optional flags `--force` and `--prune` in addition to
running the script as is with no flags.


* The `--force` flag will force the removal of a dockerized `mysql` container, It
is relevant in case you want to run the application dockerized image in `prod`
profile and you rely on a dockerized mysql image and container.


* The `--prune` option will force the removal of any exited container, whether
related to this application or not. Use it judiciously if you have other
installed containers on your local docker.


### Method 1: package and docker compose

* This nearly the easiest way (for you) to dockerize the application. The
required `Dockefile` and `docker-compose.yml` files are copied by maven to the root
directory of the project. Using this method the application runs in `dev` profile
against an embedded H2 database. Running in `prod` profile requires an additional
step, which I'll cover in the following methods.

* Since the docker supporting files are already provided here, the only thing to
really note is the docker clean-up after this stage. To perform this task we
package the project and instruct docker to compose the build.


* Dockerfile:

>
```
FROM openjdk:16.0.1-jdk-slim
EXPOSE 8080
ADD target/jpa-contacts.war jpa-contacts.war
ENTRYPOINT ["java","-jar","jpa-contacts.war"]
```


* docker-compose.yml

>
```
version: '3'
services:
  app:
    build: .
    image: jpa-contacts
    ports:
      - 8080:8080
```


#### Running


* package and docker compose:


>    * mvn clean package -DskipTests
>    * docker-compose up --build


* The application is available at [http://localhost:8080](http://localhost:8080 "http://localhost:8080")


#### Clean-Up


* The easiest way is to hit CTR+C and run the `. dkadmin.sh --clean` script:


>    * CTR+C
>    * . dkadmin.sh --clean


* The standard docker clean up routine would be rather tedious. We'll have to stop the
container running our image, find it's id, and remove it. Then find our `jpa-contacts:latest`
image and clean that up too. However, our `docker-compose.yml` file names the image and
container, so the task becomes easier:

>    * CTR+C
>    * docker container rm jpa-contacts -f
>    * docker image rm jpa-contacts:latest -f

* Occasionally, we'd want to check and remove all dangling images:


>    * docker rmi $(docker images --filter 'dangling=true' -q --no-trunc)


### Method 2: Spring Boot Buildpacks


*  The previous approach works fine but it is sub-optimal. The first problem
with the `Dockerfile` is that the jar/war file is not unpacked. There’s always a
certain amount of overhead when running a `fat` jar, and in a containerized
environment this can be noticeable. It’s generally best to unpack jars and run
in an exploded form.


*  Two new features were introduced in Spring Boot 2.3 to help improve on
these existing techniques: `buildpack support` and `layered jars`. This section
focuses on `buildpacks`. The next section on `layered jars`. In this application
the difference is made by the `pom` property `packaging.layers`. By default, it
is set to `false`. In the next part, we'll set it to `true`.


* With this `buildpacks` approach, instead of creating our `Dockerfile` and building
it using docker build or docker compose, all we have to do is call the `build-image`
goal of the [spring boot maven plugin](https://spring.io/blog/2020/01/27/creating-docker-images-with-spring-boot-2-3-0-m1 "spring boot maven plugin").


* **Note** we need to have Docker installed and running:

>    * mvn clean spring-boot:build-image -DskipTests

* We now have a fat jar (packaged as 'war') in the root directory of the
project, which spring boot maven plugin had converted to a docker image and
deployed it to our hosted docker instance.


* Next, we can verify this by listing the docker images with:


>    * docker image ls -a


* or...


>    * docker images


* We see a line for the image we've just created (in this example `3e87366e789d`
being the docker image id):

>    * REPOSITORY     TAG    IMAGE ID     CREATED      SIZE
>    * jpa-contacts   latest 1d5a1300402a 41 years ago 337MB


#### Running


* To start a container with this image we can map `localhost` port `9090` (or
any other port including `8080`) to docker port `8080` (which is the port defined
in the `application.properties` file as the web server port). Issue the command:


>    * docker run --name=jpa-contacts -it -p8080:8080 jpa-contacts:latest


* The application is available at
[http://localhost:8080](http://localhost:8080 "http://localhost:8080")


* The application is also available at
[http://172.17.0.2:8080](http://172.17.0.2:8080 "http://172.17.0.2:8080")


* Without the port mapping (e.g. the `-p8080:8080` section of the command) the
application becomes available only at <http://172.17.0.2:8080>. By default,
without any other networking flags or user intervention, docker is using the IP
range `172.17.0.0/16`. Our host is typically assigned the IP `172.17.0.1`, so the
first docker container is assigned the IP `172.17.0.2` which we use here.


* One way to know the docker container IP for sure, is to inspect it with
`[container-id]` replaced with the actual id with `docker inspect -f "{{ .NetworkSettings.IPAddress }}" [container-id]`
It is easier when the container is named, hence we named it using the
`--name=jpa-contacts` flag:


>    * docker inspect -f "{{ .NetworkSettings.IPAddress }}" jpa-contacts


* To resolve the conflict, we will later on issue the running command with
the `--net=host` flag:


>    * docker run --name=jpa-contacts --net=host -it -p8080:8080 jpa-contacts:latest


* In the above case, we won't actually need the `-p8080:8080` port mapping, since
the application's web container is configured to listen on port `8080` via the
`server.port` POM property:


>    * docker run --name=jpa-contacts --net=host -it jpa-contacts:latest


#### Clean-Up


* Again, the easiest way is to hit CTR+C and run the `. dkadmin.sh --clean` script:


>    * CTR+C
>    * . dkadmin.sh --clean


* For the sake of house keeping you can do this manually too. Remove all exited
containers, and remove the `jpa-contacts:latest` docker image, along with every
dangling left-over image (no repository, no tag):


>    * CTRL+C
>    * docker container rm jpa-contacts -f
>    * docker rmi jpa-contacts:latest -f


* Optionally:

>    * docker rmi $(docker images --filter 'dangling=true' -q --no-trunc)


#### Method 3: Spring Boot Layered Jars


*  The second issue with the first method and the `Dockerfile` is that it isn’t
very efficient if you frequently update your application. Docker images are
built in layers, and in this case your application and all its dependencies are
put into a single layer. Since you probably recompile your code more often than
you upgrade the version of Spring Boot that you use, it’s often better to
separate things a bit more. If you put jar files in the layer before your
application classes, Docker often only needs to change the very bottom layer and
can pick others up from its cache.


* A repackaged jar contains the application’s classes and dependencies in
BOOT-INF/classes and BOOT-INF/lib respectively. Similarly, an executable war
contains the application’s classes in WEB-INF/classes and dependencies in
WEB-INF/lib and WEB-INF/lib-provided. For cases where a docker image needs to be
built from the contents of a jar or war, it’s useful to be able to separate
these directories further so that they can be written into distinct layers.


* Layered archives use the same layout as a regular repackaged jar or war, but
include an additional meta-data file that describes each layer.


* By default, the following layers are defined:

>    * dependencies for any dependency whose version does not contain SNAPSHOT.
>    * spring-boot-loader for the loader classes.
>    * snapshot-dependencies for any dependency whose version contains SNAPSHOT.
>    * application for local module dependencies, application classes, and resources.


* Module dependencies are identified by looking at all of the modules that are
part of the current build. If a module dependency can only be resolved because
it has been installed into Maven’s local cache and it is not part of the current
build, it will be identified as regular dependency.


* The layers order is important as it determines how likely previous layers can
be cached when part of the application changes. The default order is
dependencies, spring-boot-loader, snapshot-dependencies, application. Content
that is least likely to change should be added first, followed by layers that
are more likely to change.


* The repackaged archive includes the layers.idx file by default. A typical
Spring Boot fat jar layout:

>
```
org/
  springframework/
    boot/
  loader/
...
BOOT-INF/
  classes/
...
lib/
...
```

* With layered jars, the structure looks similar, but we get a new `layers.idx`
file that maps each directory in the
fat jar to a layer:

>
```
- "dependencies":
  - "BOOT-INF/lib/"
- "spring-boot-loader":
  - "org/"
- "snapshot-dependencies":
- "application":
  - "BOOT-INF/classes/"
  - "BOOT-INF/classpath.idx"
  - "BOOT-INF/layers.idx"
  - "META-INF/"
```

* The goal is to place application code and third-party libraries into layers
that reflect how often they change. For example, application code is likely what
changes most frequently, so it gets its own layer. Further, each layer can
evolve on its own, and only when a layer has changed will it be rebuilt for the
Docker image.


* To set up a project to create a layered jar with Maven, we need to augment the
Spring Boot Maven Plugin configuration section in the `pom.xml` file:

>
```
<project>
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <layers>
                        <enabled>true</enabled>
                    </layers>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

* In this project the configuration is controlled by the pom property
`packaging.layers`. Here we set it to `true` and start with the project and image
building as before:


#### Running


* Change the `pom` property `packaging.layers` to `true`


* With this configuration, the Maven `package` command (along with any of its
dependent commands) will generate a new layered jar. The rest of the process is
identical to the previous method:


>    * mvn clean spring-boot:build-image -DskipTests
>    * docker run --name=jpa-contacts --net=host jpa-contacts:latest


* The difference is that we now have a layered jar (war actually) and if we
unzip the `jpa-contacts.war` and peek at it's `WEB-INF` directory content, we'll
see the `layers.idx` file with the content:

>
```
- "dependencies":
  - "WEB-INF/lib-provided/"
  - "WEB-INF/lib/"
- "spring-boot-loader":
  - "org/"
- "snapshot-dependencies":
- "application":
  - "META-INF/"
  - "WEB-INF/classes/"
  - "WEB-INF/layers.idx"
  - "WEB-INF/views/"
```

#### Clean-Up

* Hit CTR+C and run the `. dkadmin.sh --clean` script:


>    * CTR+C
>    * . dkadmin.sh --clean


* Alternatively, hit CTR+C to stop the container, remove all exited containers,
and remove the `jpa-contacts:latest` docker image, along with every dangling left
over image (no repository, no tag):


>    * CTRL+C
>    * docker rmi jpa-contacts:latest -f
>    * docker container rm jpa-contacts -f


* Optionally:


>    * docker rmi $(docker images --filter 'dangling=true' -q --no-trunc)



#### Running in `prod` profile


* While in `dev` profile the application runs against an embedded H2 database
instance, in the `prod` profile the application assumes we're running a live
MySql 8 server (possibly on a remote host). For this example, we have two
scenarios.


* **A**: Running the application on docker and connecting to a docker MySql
server container. In this case we have to note that the docker `localhost`
alias means the IP address of the docker container, not of the host machine.


* **B**: Running the application on docker and connecting to a MySql server
running on the host machine (running on `localhost:3306`) and the same host is
running docker with the application container. This is an unlikely
production scenario, but it's here for the sake of example.


#### Preparing a docker MySql Server container


* The script `dkadmin.sh` can perform all the necessary actions detailed in the
following section. It can install the required `MySql` server on docker and
perform the administrative tasks to prepare it for the application use.


* Alternatively, for the sake of example, you can do this manually.


##### Using the the `dkadmin.sh` script


* The script `dkadmin.sh` requires the docker daemon to be running and listening
on `docker.socket`. Use `sudo service docker start` or `sudo systemctl start docker` to make
sure it's running.


* running `. dkadmin.sh` with no flags will attempt to pull, install, prepare and
run a `mysql/mysql-server` (latest tag) container from DockerHub. The container
will be named `mysql` and prepared for the application and for connections to
the docker mysql shell from localhost. The mysql `root` password will be a 12
characters randomly generated string.


* running `. dkadmin.sh --password` will do the same, but will prompt the user for
a password (and verification). The password must be at least 8 characters long,
contain digit/s and at least one of the characters that appear above the digits
in a QWERTY keyboard (`@, #, &, $, %, (, ), ^, or *`).


* Note: If the given password contains a `$` sign or a `&` you'll have to
single quote the password when you login from the shell. For example:
`docker exec -it mysql mysql -u root -p'SomePass&01'`. If you login with a password
protocol (e.g. `docker exec -it mysql mysql -u root -p`), there's no need to
quote wrap the password. This is due to bash shell variable expansion when
it sees a string with a `$` sign or `&` in it.


* running `. dkadmin.sh --stop` will attempt to stop the container `mysql/mysql-server`
named `mysql` and will succeed only if such a container exists and only if it's
in a running state, otherwise the script ignores the command with a message.
You can use `. dkadmin.sh --status` before hand to check the runnig state.


* running `. dkadmin.sh --restart` is useful after a host reboot or a docker
service restart on the host machine. It will attempt to start the `mysql/mysql-server`
named `mysql` and will succeed only if such a container exists and only if it is
not already in a running state, otherwise the script ignores the command with
a message.


* running `. dkadmin.sh --clean` will clean all the images and stopped containers
but will leave the container `mysql/mysql-server` named `mysql` (no matter what
state it is in) deployed on docker.


* running `. dkadmin.sh --purge` will remove the container `mysql/mysql-server` named
`mysql` and ancestor images - if the container and images exist. The volume data
will be lost and the container will need to be re-installed for the application
to run in `prod` profile.


* running `. dkadmin.sh --status` will display a container status line


* running `. dkadmin.sh --help` will display a usage message


##### Install Manually


* If you have a MySql server running on localhost:3306 - shut it down to avoid
network address binding conflicts and confusion. Depending on your linux distro
you could use one of the following commands (the service name can vary between
`mysqld` or `mysql`):

>   * sudo service mysql stop
>   * sudo systemctl stop mysql


* If you do now wish to use `. dkadmin.sh` or `. dkadmin.sh --password` for the installation, the
manual process is rather well documented with the official
[docker documentation](https://dev.mysql.com/doc/refman/8.0/en/docker-mysql-getting-started.html "MySql on Docker"):


>   *  docker run --name=mysql --restart on-failure -d mysql/mysql-server


* If you named the container `mysql` you can view the progress via the logs with
`docker logs mysql`. You can also find out the password generated for mysql `root`.
You will need it later:


>   * docker logs mysql 2>&1 | grep GENERATED


* You can login to the mysql shell as user `root` and get prompt for the password:


>   * docker exec -it mysql mysql -uroot -p


* Through the shell you can reset the `root` password:


>   * ALTER USER 'root'@'localhost' IDENTIFIED BY 'password';


* To have shell access to your MySQL Server container, use the `docker exec -it` command
to start a bash shell inside the container:


>   * docker exec -it mysql bash
>   * mysql -u root -ppassword


* To prepare the MySql server container for the application, paste the contents of
`sql/once-as-root.sql` file into the mysql shell (as `root`)

>
```
CREATE DATABASE IF NOT EXISTS contacts;
USE `contacts`;
Create User 'user'@'localhost' Identified BY 'password';
Create User 'user'@'%' Identified BY 'password';
Grant All on contacts.* to 'user'@'localhost' with grant option;
Grant All on contacts.* to 'user'@'%' with grant option;
```

* The MySql server container is now ready for the application container to use
the `contacts` database with user `user` and password `password`.


* It is also possible to connect to the MySql container on docker from localhost. All
you need to do is find out the docker `IP`, which you can find out with:


>   * docker inspect -f "{{ .NetworkSettings.IPAddress }}" mysql


* Once you have the IP (most likely `172.17.0.2`) try (it won't work) to use the
`mysql -u root -ppassword -h 172.17.0.2` command to login.


* You should get the error message: `Access denied for user 'root'@'172.17.0.1' (using password: YES)`.
This is because of a default MySql security constraint which forbids administrative users
from logging in remotely. Docker is seeing `localhost` as a remote connection
originating from ip `172.17.0.1`. Take note of this ip. You could login with user `user` though:
`mysql -h 172.17.0.2 -u user -ppassword` because `user` is not an administrative user. Nevertheless,
we cannot run administrative tasks with a non-administrative user like `user/password`.


* To override the security constraint and allow an administrative user to connect from
`172.17.0.1`, connect to the mysql shell on docker as `root`, grant `root` the privileges to
connect from `172.17.0.1` and flush the privileges to refresh them:

>   * docker exec -it mysql bash
>   * mysql -u root -ppassword
>   * Issue the following commands:

>
```
-- DROP USER 'root'@'172.17.0.1';
CREATE USER 'root'@'172.17.0.1' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON *.* TO 'root'@'172.17.0.1' WITH GRANT OPTION;
FLUSH PRIVILEGES;
```


* You can now login to the MySql docker container with `mysql -u root -ppassword -h 172.17.0.2`.


* You can also prepare the localhost MySql server for the application with a single bash shell
command on localhost:


>   * mysql -u root -p -h 172.17.0.2 < sql/once-as-root.sql



* Exit the shell(s). The dockerized MySql container remains running.


* In the future, after a reboot or after the docker daemon had stopped, you can restart the mysql
container with:


>   * docker container restart mysql


* ...and log to the bash shell with:


>   * docker exec -it mysql bin/bash


* ...and find out the docker IP with:


>   * docker inspect -f "{{ .NetworkSettings.IPAddress }}" mysql


* ...and then connect to the docker mysql server with:


>   * mysql -h 172.17.0.2 -u user -ppassword contacts


* or:


>   * mysql -h 172.17.0.2 -u root -ppassword


#### Running


* Build the project in `prod` profile (if not built already), dockerize and run
it:

>    * mvn clean spring-boot:build-image -Pprod -DskipTests
>    * docker run --name=jpa-contacts --net=host -e "spring.datasource.url=jdbc:mysql://172.17.0.2:3306/contacts" -it -p8080:8080 jpa-contacts:latest


#### Running in `prod` profile with MySql running on localhost


* This is pretty much the same as before, except we don't need to a
MySql docker container. We only have to make sure our localhost MySql
server is running.


* If the docker MySql container `mysql/mysql-server` is running, then stop it
(easiest with `. dkadmin --stop`) or:


>    * docker stop $(docker ps -a -q --filter ancestor=mysql/mysql-server)


* We need to make sure our localhost MySql server is running. Start the MySql
server on `localhost`. Use one of the following commands (the service name is
usually one of `mysql` or `mysqld`):


>    * sudo service mysql start
>    * sudo systemctl start mysql


* Build the project in `prod` profile, dockerize and run it:

>    * mvn clean spring-boot:build-image -Pprod -DskipTests
>    * docker run --name=jpa-contacts --net=host -it -p8080:8080 jpa-contacts:latest


* The application is available on <http://localhost:8080>


#### Clean-Up


* We want to remove the application container:


>    * CTRL+C
>    * docker container rm jpa-contacts -f


#### Reverting to `dev` profile


* We can always rebuild the project in `dev` profile, but we don't want to
do that on production and cloud environments.


* We can, instead, use the `-e "SPRING_PROFILES_ACTIVE=dev"` flag in the `docker run` for passing the
environment variable `SPRING_PROFILES_ACTIVE` to docker. We can stop the docker MySql container and
run with `dev` profile because we'll be running against an embedded H2 database server.

* We can stop the application, and optionally stop the MySql server on local host and docker
(they won't be utilized anyway), and run the application with profile `dev`:


>    * docker run --name=jpa-contacts --net=host -e "SPRING_PROFILES_ACTIVE=dev" -it -p8080:8080 jpa-contacts:latest


* The `--net=host` means the docker container network is now the same as our host
so `localhost` in docker is the same as `localhost` on our hosting machine.


* The application is available on <http://localhost:8080>


* Naturally, we can revert back to `prod` profile after restarting the MySql container:


>    * Hit CTR+C to stop the running application
>    * docker container rm jpa-contacts -f
>    * docker run --name=jpa-contacts --net=host -e "SPRING_PROFILES_ACTIVE=prod" -e "spring.datasource.url=jdbc:mysql://172.17.0.2:3306/contacts" -it -p8080:8080 jpa-contacts:latest


#### Clean-Up


* Hit CTR+C to stop the container and run `. dkadmin.sh --clean` or manually remove all exited
containers, and remove the `jpa-contacts:latest` docker image, along with every dangling left-over
image (no repository, no tag):


>    * CTRL+C
>    * docker stop $(docker ps -a -q --filter ancestor=mysql)
>    * docker rmi jpa-contacts:latest -f
>    * docker rmi $(docker images --filter 'dangling=true' -q --no-trunc)
>    * docker container prune -f


#### Method 4: Containerizing with JIB


* The main down side to the previous methods discussed was that we actually
needed a docker daemon running so that spring boot maven plugin can deploy the
image to it. With `Jib` we don't even need a docker daemon. We can build the
images and store them on a repository for later `pull` (perhaps from a cloud
environment, maybe from a remote host). We can, also, like before, install an
image on docker.


* Jib builds optimized Docker and OCI images for your Java applications without
a Docker daemon - and without deep mastery of Docker best-practices. It is
available as plugins for Maven and Gradle and as a Java library.


* Jib is included in this project as a maven plugin. It is possible to configure
the plugin to automatically build a docker image upon project build using
`execution` configuration::

>
```
<executions>
	<execution>
		<phase>package</phase>
		<goals>
			<goal>dockerBuild</goal>
		</goals>
	</execution>
</executions>
```


* To avoid a maven `package` phase clutter however, we'll be calling maven Jib
goals manually from command line (for example: `mvn jib:dockerBuild`).



* To run the application leave your MySql server on localhost running - or deploy
a dockerized MySql image (easiest with `. dkadmin.sh`).


#### Running (`prod` profile)


* Using the Jib `dockerBuild` and running the application:

>    * mvn clean package jib:dockerBuild -Pprod -DskipTests
>    * docker run --name=jpa-contacts --net=host -e "spring.datasource.url=jdbc:mysql://172.17.0.2:3306/contacts" -it -p8080:8080 jpa-contacts:latest



#### Reverting to `dev` profile


* Same as before...

>    * Hit CTR+C to stop the running application
>    * docker container rm jpa-contacts -f
>    * docker run --name=jpa-contacts --net=host -e "SPRING_PROFILES_ACTIVE=dev" -it -p8080:8080 jpa-contacts:latest

* The application is available on <http://localhost:8080>


#### Clean-Up


* We want to stop and remove mysql, all containers that ran our application
image `jpa-contacts:latest`, the application image and all the left over 'garbage'
dangling images. The easiest way is with `. dkadmin.sh --clean`
and `. dkadmin.sh --purge`, but it can be done manually:


>    * CTRL+C
>    * docker container rm $(docker ps -a -q --filter ancestor=jpa-contacts) -f
>    * docker rmi jpa-contacts:latest -f
>    * docker container rm  mysql -f


#### Method 5: Pushing and pulling an image with JIB


* The primary JIB goal `jib:build`, which builds a docker image and uploads it to
a docker repository like [DockerHub](https://hub.docker.com/ "DockerHub"). To
use this goal you'll need to open an account and have a repository. You'll also
need to configure the credentials for JIB to authenticate and authorize access
to your repository.


##### Supplying Repository Credentials to JIB


*  For the `jib:build` goal, the easiest way is to provide credentials through
maven in .m2/settings.xml (I'm assuming the use of a [DockerHub](https://hub.docker.com/ "DockerHub") repository in this example):

>
```
<servers>
    <server>
        <id>registry.hub.docker.com</id>
        <username><DockerHub Username></username>
        <password><DockerHub Password></password>
    </server>
</servers>
```

* **Note** however: The recommended way by Google to provide the credentials is
to use helper tools, which can store the credentials in an encrypted format in
the file system. We can use `docker-credential-helpers` instead of storing plain-text
credentials in settings.xml, which is much safer. See for example [docker-credential-helpers](https://github.com/docker/docker-credential-helpers "docker-credential-helpers").


* The process of installing the `docker-credential-helpers` and configuring an
encrypted password for Jib to use, may get tedious and cumbersome. [Geoff Hudik](https://geoffhudik.com/tech/author/thnk2win/ "Geoff Hudik")
has a script for installing and configuring [docker pass credential helper on Ubuntu](https://geoffhudik.com/tech/2020/09/15/docker-pass-credential-helper-on-ubuntu/ "docker pass credential helper on Ubuntu"), which simplifies the task.
The script is included in the `docker-credential-helpers` directory at the root of
this project. You can run it as `root` using `sudo` and it will do the
installations and prompt you for input when needed.


##### Pushing and image to a repository:


* To push an image to your repository, configure the jib maven plugin `image`
element inside the `to` destination element, with the repository url prefix and
user. For example, this is the configurations for my own DockerHub repository
at `registry.hub.docker.com`:

>
```
<plugin>
	<groupId>com.google.cloud.tools</groupId>
	<artifactId>jib-maven-plugin</artifactId>
	<configuration>
		<to>
			<image>registry.hub.docker.com/tnsilver/${project.artifactId}</image>
			<!-- <image>${project.artifactId}</image> -->
		</to>
		<container>
			<ports>
				<port>${server.port}</port>
			</ports>
			<!-- <environment><SPRING_PROFILES_ACTIVE>${spring.profiles.active}</SPRING_PROFILES_ACTIVE></environment> -->
		</container>
	</configuration>
</plugin>
```

* To push the image to `registry.hub.docker.com` all we have to do is (and wait...
this can take some time):


>    * mvn clean package jib:build -DskipTests


* If the process gets stuck for network latency or Jib had lost a connection
with the repository, all you have to do is `mvn jib:build` and it'll pick up from
where it had left. Uploading those hundreds of megabytes can take some time.


* There's no cleanup! We don't even need the docker daemon installed on
localhost. Once the image is saved to the repository, anyone (authorized)
can install the image on a docker daemon anywhere. It is currently a public
repository in my DockeHub so no authorization is required other than logging
in with docker (`docker login`).


##### Pulling and running an image from a repository:

* Clean the previous application related docker images and containers (the
easiest way is with `. dkadmin.sh --clean` and `. dkadmin.sh --purge`, or at least
`. dkadmin.sh --stop`).


* The basic pull command (no need to run it) is:


>    * docker pull tnsilver/jpa-contacts:latest


#### Running


* We don't actually have to pull the image though. Once we tell docker to run
the `jpa-contacts:latest` image and it won't be found locally, docker will attempt
to find it in the DockerHub repository. We can simply do with:


>    * docker run --name=jpa-contacts --net=host -e "SPRING_PROFILES_ACTIVE=dev" -it tnsilver/jpa-contacts:latest



#### Clean-Up


* Nothing here we haven't seen before:

>    * docker container rm $(docker ps -a -q --filter ancestor=tnsilver/jpa-contacts:latest) -f
>    * docker rmi tnsilver/jpa-contacts:latest -f
>    * docker rmi $(docker images --filter 'dangling=true' -q --no-trunc)


## Credits

* Many thousands of developers from Sun Microsystems, Oracle, IBM, Microsoft,
Google, Netflix, Amazon, and people in the Apache foundation, Spring IO,
Eclipse, IntelliJ, Maven plugin developers, the JQuery team and many others -
all deserve credit for the open source libraries I use.


* Special thanks to [Geoff Hudik](https://geoffhudik.com/tech/author/thnk2win/ "Geoff Hudik")
for the [Docker pass Credential Helper on Ubuntu](https://geoffhudik.com/tech/2020/09/15/docker-pass-credential-helper-on-ubuntu/ "Docker pass Credential Helper on Ubuntu")


* and...


![T.N.Silverman](../images/me.jpg?raw=true "T.N.Silverman")

* All java code, property files, client Java Scripts, JSP's and HTML templates
created by [T.N. Silverman](https://github.com/tnsilver "About T.N.Silverman")


## License

[Licensed under the Apache License, Version 2.0](LICENSE.md "Apache License")
