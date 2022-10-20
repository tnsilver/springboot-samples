# Using Redis as an RDBMS

**New** Responsive bootstrap HTML5 templates. Now works on mobile devices, tablets... etc.

![Contacts Application](../images/redis-phone.jpg?raw=true "Responsive Contacts Application")

### A Docker Ready Spring Boot RESTful CRUD Web App Demo with Redis

## What's in here?

A Docker ready SpringBoot CRUD (create, retrieve, update delete) RESTful (with a JavaScript client) web application with
a master/details UI to manage a list of contacts with **Redis** serving as the persistence storage and acting as an
RDBMS. This application is treating `Contact` hashes much like JPA entities. It even generates sequential numeric primary
key like hash id's for it's hashes. Here you can also see how JSP's and Thymeleaf templates reside together and are
being served from a fat spring boot jar (war actually). This README file will walk you through some basic to advanced
methods of dockerizing the application.


## Table of contents


* [Getting Started](#getting-started)
* [Quick Redis Client Reference](#quick-redis-client-reference)
* [Preface: About Redis as an RDBMS](#having-redis-function-as-an-rdbms-with-spring-boot)
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


![Contacts List Redis](../images/redis/contacts-redis.jpg?raw=true "Redis Contacts")


![Add Redis Contact](../images/redis/contacts-redis-add.jpg?raw=true "Add Redis Contact")


![About Redis Contact](../images/redis/contacts-redis-about.jpg?raw=true "About Redis Contact")


## Getting Started


* `cd` into the project's root directory from a command line shell


* type: `./mvnw clean package spring-boot:run -DskipTests` to run the application


* or type: `./mvnw clean test` to test the application


* or: `./mvnw clean test -Dtest=ApplicationIT -Ptest` to run the application in `test` profile (runs for 5 minutes and exits)


* go to [http://localhost:8080](http://localhost:8080 "http://localhost:8080") to use the application


* see [Testing and running the applications from a shell window](../README.md#testing-and-running-the-applications-from-a-shell-window) for details


## Quick Redis Client Reference


* start redis


> `sudo service redis start`


* flash (clean all) database at index 0


> `redis-cli -n 0 FLUSHDB`


* login to redis client (database 0)


> `redis-cli -n 0`


* count contacts by key prefix


> `SCARD contact`


* check keys matching a pattern exist:


> `EXISTS contact:1`


*  get all keys matching a pattern:


> `KEYS contact:[0-9]`


*  delete all keys


> `DEL contact`


* flush all hashes (clears database)


> `FLUSHALL ASYNC` or simply `FLUSHALL`


## Having Redis function as an RDBMS with Spring Boot


If you're new to Redis like I was when I thought up this project, there's a number of issues you should be aware of.
Redis is not an RDBMS and does not behave like one. If you want to make it act like an RDBMS there's a myriad of issues
to overcome:


* 1. Redis does not look up relations between 'entities', in fact, it doesn't have 'entities'. It is a key/value storage
and when you mimic 'entities' in the form of `hashes`, which is what Redis serializes an stores, it can only look up
indexed hashed `keys`, which is your responsibility to index (off course there's redis/sprig-boot annotations to make
this easy, but it's not the same as JPA entities).


* 2. When you'll want to 'query' redis like JPA, you'll soon discover that it's very limited. Because redis looks up
only indexed hashed keys, the so called 'query' runs against the keys but not their values. This means that you have an
API that can only look up keys that are either matching your criteria in full (exact match) or match a prefix (ends
with). If you're thinking 'criteria query' or 'find by example' like any natural JPA developer would, think again!
Your example/criteria can only match keys in full, or match just the prefix of the key. If, for example, you want to
look up the value 'Tom', which is a first name in a redis hash under the keyspace `contact`, you'll need to be aware
that 'Tom' is actually stored in some indexed key `contact:firstName:Tom` and is a key/value (like 'propery' in JPA)
of hash key `contact:35` (with 35 being an indexed hash id). Hence, no query by example, or at least it's very limited.


* 3. No query language, no Spring integration `@Query` annotation for redis. Apparently not a big deal but in reality
this will make your life a hell of a lot harder, even with Spring Boot. The reason for this is that you can no longer
augment the Spring Boot repositories beyond the standard `findByProperty1AndProperty2` methods. To implement a custom
`searchByParams` method, you'll need to define your own custom interface, make both the Spring (CrudRepository or
PagingAndSortingRepository) extending interface implement your custom repository interface and then you'll need to
provide an implementation for it. So... for example, if you're dealing with contacts, like this application, you'll
need:


		*   CustomContactRepository (define custom methods)
		*   ContactRepository (extends PagingAndSortingRepository and CustomContactRepository)
		*   ContactRepositoyImpl (implementation of both interfaces - likely using RedisTemplate to do the actual work).


* 4. Coding your own repositories isn't as hard as it sounds. If you've done one repository, you'll likely find that
implementing the next one is a much easier (and mostly copy & paste) task. You can also use your Spring Boot generated
repository implementation inside your own by injecting it, and that makes the CRUD logic implementation very easy - but
it's just the beginning of your problems in a RESTful Web Application.


* 5. You don't get Spring Boot REST support when you implement your own repository! Did you know that? Well, you just
don't! Your custom repository methods are not exposed, profiled or even support HATEOAS. If you want HATEOAS support or
worst, rely on it - like my Java Script client does, you're in a world of tedious coding to implement all that nice
`application/hal+jason` support on your own. You're going to need a `RepresentationModel` for your own model classes
and a `RepresentationModelAssembler` which extends `RepresentationModelAssemblerSupport` to assemble those model classes
into a hal/HATEOAS representational form with all the nifty links for `_self` and `href` and the whole `_embedded`
hal+json response shebang. That's your responsibility now.


* 6. Once you've done that... There's no escaping a REST Controller. While with JPA (or MongoDB) your spring boot
generated repositories REST API is automatically exposed, this is not the case with custom repositories that you're
forced to use with Redis. So if you want REST clients to use your API, you'll need to expose it with a REST Controller
and methods that correspond to the REST verbs. You're in charge of returning the `ResponsEntity<Page<Model>>` from the
controller methods, with the correct response status for each and every case.


* 7. Redis doesn't have a `@GeneratedValue(strategy = GenerationType.IDENTITY)` for it's 'models' (annotated with
`@RedisHash`) and there's no built in mechanism to generate nice sequential numeric ID's. What you'll (horribly)
discover is a dual problem. When you store a hash, Spring Boot generates some UUID for a String id property and some
arbitrary Long number (sometimes negative) for numeric id's. Redis in turn will generate it's own unique id key. If you
curl or use `postman` (which I don't use) to your API and finally manage to get a `application/hal+json` response,
you'll  see the id of your stored hash does not match the HATEOAS links. I've solved that issue too by using stored
sequence hashes, which I increment when I save a new redis hash. Those sequences are stored in Redis too. This is very
similar to the solution I provided with MongoDB. It didn't come cheap. I've spent a lot of time on this one...


* 8. Populating the Redis repository with test or live data... Well, forget about `Flyway` and the `Liquibase` marketing
promises. They don't support Redis. If you want to load data, convert it to json and use some extension of
`Jackson2RepositoryPopulatorFactoryBean`. I've done that too.


* 9. Turning Redis into an RDBMS is a world of misery! If that's what you need on production, do your
self a favour and use an RDBMS or a different no-sql solution, like MongoDB. If you want to see how it's done anyways,
well... lucky for you - I've done all of it here. Just have a look and adjust it for your needs as you go a long. Lemme
know what you think.


## Installing, testing and running

*  See the [parent README](../README.md "parent README.md") file for installation details.


*  The `curl` commands in this README file end with piping the `jq .` command. JQ is a `json` processor, which
allows for pretty printing the json responses.


* Follow the [official jq installation guide](https://stedolan.github.io/jq/download/ "jq installation") to install
JQ for your operating system.


## Executive Summary

* from the root directory of this project:


* run: `./mvnw clean package spring-boot:run -Pdev -DskipTests`


* goto [http://localhost:8080](http://localhost:8080 "http://localhost:8080")


* Have fun!

## Testing the REST API with curl

This _RESTful_ application uses a **REST API** to manage contacts. A JavaScript client performs
the REST calls to the API via JQuery ajax'ing. To catch a glimpse of this _RESTful_ API without the
client scripts, for testing purposes, you can issue `curl` commands to the API end points:

* Start by running the application in `dev` profile from command line:

> mvn clean package spring-boot:run -Pdev -DskipTests


## Exporting the host address to a local environment variable

* In a new shell (command line) window, export the host URL to ease the **curl** command usage:

> export HOST=localhost:8080


## Performing REST queries with curl

* then you can start to issue `curl` commands, for examle, to test the functionality of the `HelloRestController`,
which is a simple REST controller that does not involve any persistence operations. It basically just greets the user:


* To say hello to 'Homer':

> curl -v -H "Content-Type: application/json" -X GET $HOST/api/hello/Homer | jq .


* To say hello to 'Homer' 'Simpson':

> * curl -v -H "Content-Type: application/json" -X GET  $HOST/api/hello/Homer/Simpason | jq .


* Now, query the contacts profile provided by ALPS. Notice the `application/alps+json` response type.
This exposes the available public REST API:


> * curl -v -G $HOST/api/profile/contacts | jq .


* next, query the paged contacts. Notice the `application/hal+json` response type and the paged response:

> * curl -v  -G -k "$HOST/api/contacts?size=100" | jq .


* You can use the redis (rather limited functionality) of querying by example if you make a call to the
`/api/contacts/search/byExample` search by example functionality. Notice the body of the request including the complete
first and last name `Homer` and `Simpson`:

>
```
curl  -v -u user:password -X POST $HOST/api/contacts/search/byExample \
-H "Content-Type: application/json" \
-H "Accept: application/hal+json" \
-H "Cache-Control: no-cache" \
-d \
'{"firstName":"Homer",
  "lastName":"Simpson"}' | jq .
```

* Redis does not support a `like %key%` search functionality as with JPA and SQL. It can only look up exact matches or
keys ending with the search criteria string. If we try to look up by an example consisting of a partial first name,
like `H` for `Homer`, it will fail to yield any results:

>
```
curl  -v -u user:password -X POST $HOST/api/contacts/search/byExample \
-H "Content-Type: application/json" \
-H "Accept: application/hal+json" \
-H "Cache-Control: no-cache" \
-d \
'{"firstName":"H",
  "lastName":"Simpson"}' | jq .
```

* If you want a more elaborate search functionality, you're going to have to implement it on your own. You can use the
searching function `byParams` to execute a query by parameters and find contacts with, for example, having `Simp` in
for their last name. Notice the **pagination parameters** at the end of the `curl` URL, and the pagination data at
the end of the `application/hal+json` response:

>
```
curl -X "GET" -v -H "Content-Type: application/json" -H "Cache-Control: no-cache" \
-k "$HOST/api/contacts/search/byParams?ssn=&firstName=&lastName=Simp&birthDate=&married=&children=&page=0&size=5&sort=id,ASC" \
| jq .
```

* This is a rather powerful search method so we can use a more complex filter to find only the children of
the `Simpson` family:

>
```
curl -X "GET" -v -H "Content-Type: application/json" -H "Cache-Control: no-cache" \
-k "$HOST/api/contacts/search/byParams?ssn=&firstName=&lastName=Si&birthDate=&married=false&children=0&page=0&size=5&sort=id,ASC" \
| jq .
```

* It also supports dates, so we can find `Homer Simpson` by a `birthDate` parameter:

>
```
curl -X "GET" -v -H "Content-Type: application/json" -H "Cache-Control: no-cache" \
-k "$HOST/api/contacts/search/byParams?ssn=&firstName=&lastName=Si&birthDate=1978-06-20&married=true&children=3&page=0&size=5&sort=id,ASC" \
| jq .
```

* We don't actually need to supply all the parameters except those of fine tuned criteria.
To find `Homer` by his birth date, we can simply call::

>
```
curl -X "GET" -v \
-H "Content-Type: application/json" \
-H "Cache-Control: no-cache" \
-k "$HOST/api/contacts/search/byParams?birthDate=1978-06-20" | jq .
````

* Spring Boot will provide a default unsorted page (size 10) in this case where we don't specify any pagination
criteria. It is a very powerful method. In fact, this exact same method `byParams` is used by the java script client
in the contacts list master page in the application. Users can filter the criteria to find and choose the sorting
property, direction and page size, and JQuery just collects the parameters and uses ajax to call the REST API of
the application very similarly to the previous `curl` commands.


## Performing data manipulation with curl

Since Spring Security is authorizing requests and does not allow anonymous access to data manipulation operations via the appropriate `POST`, `PUT`, `PATCH` or `DELETE` REST `http methods`, from now on all `curl` commands must include the `-u user:password` basic authentication data.


* The following curl commands require disabling `csrf protection` in `SecurityConfig` which it does for the `test`
and `dev` profiles.


* We can **INSERT** (create or save) a new contact with `curl` and the `POST` http method. Notice the
json (`-d`) data format and the http headers, each with the `-H` flag. The response status is `201`
(created) and the response Content-Type is `application/hal+json`.

>
```
curl  -v -u user:password -X POST $HOST/api/contacts \
-H "Content-Type: application/json" \
-d \
'{"ssn":{"ssn":"987-56-4231"},
  "firstName":"Lucky",
  "lastName":"Luciano",
  "birthDate":"1897-11-24",
  "married": true,
  "children":"0"}' | jq .
```

* Now we can find newly inserted Mr. Luciano with the following query by example:

>
```
curl  -v -u user:password -X POST $HOST/api/contacts/search/byExample \
-H "Content-Type: application/json" \
-d \
'{"ssn":{"ssn":"987-56-4231"}}' | jq .
```


* We can **UPDATE** a new contact with `curl` and the `PUT` http method.
The response status is `200` (OK) and the response Content-Type is `application/hal+json`.

>
```
curl  -v -u user:password -X PUT $HOST/api/contacts/35 \
-H "Content-Type: application/json" \
-d \
'{"id": 35,
  "ssn":{"ssn":"987-56-4231"},
  "lastName":"Luke",
  "birthDate":"1946-11-24",
  "married": false,
  "children":"0"}' | jq .
```

* ...after this our mobster had changed his last name to the friendly 'Luke', and we can keep going to grant him
a completely new identity:


```
curl  -v -u user:password -X PUT $HOST/api/contacts/35 \
-H "Content-Type: application/json" \
-d \
'{"ssn":{"ssn":"123-45-5789"},
  "firstName":"Joky",
  "lastName":"Joker"}' | jq .
```

* We can also **UPDATE** just the changes applied to an existing contact with `curl` and the `PATCH` http method. Here
we can supply only the delta of changes (last name and birth date in this case). The
response status is `200` (OK) and the response Content-Type is `application/hal+json`.

>
```
curl  -v -u user:password -X PATCH $HOST/api/contacts/35 \
-H "Content-Type: application/json" \
-d '{"firstName":"Test","lastName":"Tester", "birthDate": "1897-11-24"}' | jq .
```

* Finally... we don't know who that contact is anymore, so we can delete them with `curl` and the `DELETE` http method.
There's no response body after deletion, but the response status is `204` (deleted)

>
```
curl -v -u user:password -X DELETE "$HOST/api/contacts/35"
```

* if we try that again, the response status is `404` (not found), which is expected as we've just deleted the contact.

>
```
curl -v -u user:password -X DELETE "$HOST/api/contacts/35"
```

* If we need to restore the deleted contact, we use `curl` to `POST` it again. Notice the id of
the contact is now different than before. Again, the response status is `201` (created) and the response Content-Type
is `application/hal+json`.

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

* We can query all the contacts and see the new id. Notice the gap between the next to last contact (`id`: 34) and
our new contact (`id`: 36). The gap was created when we initially deleted the contact. The identity property of the
contacts is unique and non-restoreable in this application.

>
```
curl -v  -k "$HOST/api/contacts?size=100" | jq .
```

## Containerizing the Application with Docker


* The next section talks about 5 methods to create an OCI image and deploy the application to docker. These are not the
only ways to achieve the same goal, but they sum up the basics. The `Clean-Up` routine in each section is specific to
the described method, but at any time you can run the bash script `dkclean.sh` from the root directory of the project. The
script will clean up docker and remove the images and containers added with each method used.


* The `dkclean.sh` accepts the optional flags `--force` and `--prune` in addition to running the script as is with no flags.


* The `--force` flag will force the removal of a dockerized `redis` container, It is relevant in case you want to run the
application dockerized image in `prod` profile and you rely on a dockerized redis image and container.


* The `--prune` option will force the removal of any exited container, whether related to this application or not. Use it
judiciously if you have other installed containers on your local docker.


### Method 1: package and docker compose

* This is likely the easiest way (for you) to dockerize the application. The required `Dockefile` and `docker-compose.yml`
files are already filtered and copied by maven to the root directory of the project. Using this method the application
runs in `dev` profile against an embedded redis server. Running in `prod` profile requires an additional step, which I'll
cover in the following methods. Since the docker supporting files are already provided here, the only thing to really
note is  the docker clean-up after this stage. To perform this task we package the project and instruct docker to
compose the build.


* Dockerfile:

>
```
FROM openjdk:16.0.1-jdk-slim
EXPOSE 8080
ADD target/redis-contacts.war redis-contacts.war
ENTRYPOINT ["java","-jar","redis-contacts.war"]
```


* docker-compose.yml

>
```
version: '3'
services:
  app:
    build: .
    image: redis-contacts
    ports:
      - 8080:8080
```


#### Running


* package and docker compose:


>    * mvn clean package -DskipTests
>    * docker-compose up --build


* The application is available at [http://localhost:8080](http://localhost:8080 "http://localhost:8080")


#### Clean-Up


* The standard docker clean up routine would be rather tedious. We'll have to stop the container running our image, find
it's id, and remove it. Then find our `redis-contacts:latest` image and clean it up too.

>    * CTR+C
>    * docker container ls -a
>    * docker container stop [container-id]
>    * docker container rm [container-id]
>    * docker image ls -a
>    * docker image rm [image-id]


* Fortunately we can take a **clean up shortcut** and Hit CTR+C to stop the container, remove all exited containers,
and the docker image, along with every dangling left-over image (no repository, no tag):


>    * CTRL+C
>    * docker container prune -f
>    * docker rmi redis-contacts:latest -f


* Occasionally, we'd want to check and remove all dangling images and we can do it like so:

>    * docker rmi $(docker images --filter 'dangling=true' -q --no-trunc)


### Method 2: Spring Boot Buildpacks

*  The previous approach works fine but it is sub-optimal. The first problem with the Dockerfile is that the jar/war
file is not unpacked. There’s always a certain amount of overhead when running a fat jar, and in a containerized
environment this can be noticeable. It’s generally best to unpack jars and run in an exploded form.


*  Two new features were introduced in Spring Boot 2.3.0.M1 to help improve on these existing techniques: buildpack
support and layered jars. This method focuses on buildpacks, but in the application the only difference which makes
this a non-layered buildpack build is the pom property `packaging.layers`. In the next part, we'll set it to `true`.


* With this approach, instead of creating our `Dockerfile` and building it using docker build or docker compose, all we
have to do is call the `build-image` goal of the [spring boot maven plugin](https://spring.io/blog/2020/01/27/creating-docker-images-with-spring-boot-2-3-0-m1 "spring boot maven plugin"). **Note** we need to have Docker installed and running:

>    * mvn clean spring-boot:build-image -DskipTests

* We now have a fat jar (packaged as 'war') in the root directory of the project, which spring boot maven plugin had
converted to a docker image and deployed it to our hosted docker instance.


* Next, we can verify this by listing the docker images with:


>    * docker image ls -a


* We see a line for the image we've just created (in this example `3e87366e789d` being the docker image id):

>    * REPOSITORY     TAG    IMAGE ID     CREATED      SIZE
>    * redis-contacts latest 2a52f92a0221 41 years ago 333MB


* We have to note docker is not running the same network as our own host machine. The alias 'localhost' for docker does not resolve to 127.0.0.1 like our host machine. In fact, docker is likely seeing localhost as some IP between 172.17.0.1 and 172.17.0.16. Check `ifconfig` to verify what IP is assigned to docker0.


#### Running


* To start a container with this image we can map out localhost port `9090` (or any other port including `8080`) to port
`8080` (which is the port defined in the `application.properties` file as the web server port). Issue the command:


>    * docker run -it -p8080:8080 redis-contacts:latest


* The application is available at [http://localhost:8080](http://localhost:8080 "http://localhost:8080")


* The application is also available at [http://172.17.0.2:8080](http://172.17.0.2:8080 "http://localhost:8080")


* Without the port mapping (e.g. the `-p8080:8080` section of the command) the application becomes available only at <http://172.17.0.2:8080>.
By default, without any other networking flags or user intervention, docker is using the IP range `172.17.0.0/16`. Our host is
typically assigned the IP `172.17.0.1`, so the first docker container is assigned the IP `172.17.0.2` which we use here.


* One way to know the docker container IP for sure, is to inspect it (with `[container-id]` replaced with the actual id:


>    * docker inspect -f "{{ .NetworkSettings.IPAddress }}" [container-id]


* To resolve the conflict, we will later on issue the running command with the `--net=host` flag:


>    * docker run --net=host -it -p8080:8080 redis-contacts:latest


* In the above case, we won't actually need the `-p8080:8080` port mapping, since the application's web container is
configured to listen on port `8080` via the `server.port` POM property:


>    * docker run --net=host -it redis-contacts:latest


#### Clean-Up


* Hit CTR+C to stop the container, remove all exited containers, and remove the `redis-contacts:latest` docker
image, along with every dangling left-over image (no repository, no tag):


>    * CTRL+C
>    * docker container prune -f
>    * docker rmi redis-contacts:latest -f


* Optionally:

>    * docker rmi $(docker images --filter 'dangling=true' -q --no-trunc)


#### Method 3: Spring Boot Layered Jars


*  The second issue with the first method and the `Dockerfile` is that it isn’t very efficient if you frequently update
your application. Docker images are built in layers, and in this case your application and all its dependencies are
put into a single layer. Since you probably recompile your code more often than you upgrade the version of Spring Boot
that you use, it’s often better to separate things a bit more. If you put jar files in the layer before your application
classes, Docker often only needs to change the very bottom layer and can pick others up from its cache.


* A repackaged jar contains the application’s classes and dependencies in BOOT-INF/classes and BOOT-INF/lib
respectively. Similarly, an executable war contains the application’s classes in WEB-INF/classes and dependencies in
WEB-INF/lib and WEB-INF/lib-provided. For cases where a docker image needs to be built from the contents of a jar or
war, it’s useful to be able to separate these directories further so that they can be written into distinct layers.


* Layered archives use the same layout as a regular repackaged jar or war, but include an additional meta-data file
that describes each layer.


* By default, the following layers are defined:

>    * dependencies for any dependency whose version does not contain SNAPSHOT.
>    * spring-boot-loader for the loader classes.
>    * snapshot-dependencies for any dependency whose version contains SNAPSHOT.
>    * application for local module dependencies, application classes, and resources.


* Module dependencies are identified by looking at all of the modules that are part of the current build. If a module
dependency can only be resolved because it has been installed into Maven’s local cache and it is not part of the current
build, it will be identified as regular dependency.


* The layers order is important as it determines how likely previous layers can be cached when part of the application
changes. The default order is dependencies, spring-boot-loader, snapshot-dependencies, application. Content that is
least likely to change should be added first, followed by layers that are more likely to change.


* The repackaged archive includes the layers.idx file by default. A typical Spring Boot fat jar layout:

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

* With layered jars, the structure looks similar, but we get a new `layers.idx` file that maps each directory in the
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

* The goal is to place application code and third-party libraries into layers that reflect how often they change. For
example, application code is likely what changes most frequently, so it gets its own layer. Further, each layer can
evolve on its own, and only when a layer has changed will it be rebuilt for the Docker image.


* To set up a project to create a layered jar with Maven, we need to augment the Spring Boot Maven Plugin configuration
section in the `pom.xml` file:

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

* In this project the configuration is controlled by the pom property `packaging.layers`. Here we set it to `true` and start
with the project and image building as before:


#### Running


* With this configuration, the Maven `package` command (along with any of its dependent commands) will generate a new
layered jar. The rest of the process is identical to the previous method:


>    * mvn clean spring-boot:build-image -DskipTests
>    * docker run --net=host redis-contacts:latest


* The difference is that we now have a layered jar (war actually) and if we unzip the `redis-contacts.war` and peek at
it's `WEB-INF` directory content, we'll see the `layers.idx` file with the content:

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


* Hit CTR+C to stop the container, remove all exited containers, and remove the `redis-contacts:latest` docker
image, along with every dangling left-over image (no repository, no tag):


>    * CTRL+C
>    * docker container prune -f
>    * docker rmi redis-contacts:latest -f


* Optionally:

>    * docker rmi $(docker images --filter 'dangling=true' -q --no-trunc)



#### Running in `prod` profile


* While in `dev` profile the application runs against an embedded redis instance, in the `prod` profile the application
assumes we're running a live redis server (possibly on a remote host) to connect to. For this example, we have two
scenarios.


* **A**: Running the application on docker and connecting to a live redis instance which also runs on docker. In this
case we have to note that the docker container 'localhost' alias means the IP address of the docker container, not of
the docker host machine.


* **B**: Running the application on docker and connecting to a redis instance which runs on the host machine (i.e, we
have a redis instance running on default localhost:6379) and the same 'localhost' is running docker, where the
application container is running). This is an unlikely production scenario - but it's here for the sake of example.


* If you have a redis server running on localhost - shut it down to avoid network address binding conflicts
and confusion. Depending on your linux distro you could use on of the following commands:

>   * sudo service redis stop
>   * sudo systemctl stop redis


* Install redis on docker. This is rather easy and quick, except we'll need to configure the dockerized redis to listen
on all network interfaces. For this end, we'll have to supply a `redis.conf` file, with the `bind` directive either
commented out or set to `bind 0.0.0.0`.

* When maven builds the project this file is copied to the project root directory, and when we install redis on docker
we supply the `absolute` path to it like this `docker run --name redis-server -p 6379:6379 -v /path/to/redis.conf:/etc/redis/redis.conf -d redis`


* For example: On my Ubuntu focal box this translates to:

>   * docker run --name redis-server -p 6379:6379 -v /data/workspaces/jee-2021-06-R/springboot-samples/springboot-rest-redis-contacts/redis.conf:/etc/redis/redis.conf -d redis


* **Optional**: Test the dockerized redis server:

>    * docker exec -it redis-server sh

* **Optional**: Now you can connect to the `redis-cli` and test the dockerized redis. You should be getting a **PONG**
response:


>    * redis-cli -n 0 ping


* **Note**: It is possible to actually connect to this redis server from the locahost machine (`redis-cli -n 0 -h 0.0.0.0`
or just `redis-cli -n 0`), which makes this one easy method to install a redis server for development.


* Exit the shell. The redis dockerized container remains running for the next stage.

* Build the project in `prod` profile (if not built already), dockerize and run it:

>    * mvn clean spring-boot:build-image -Pprod -DskipTests
>    * docker run --net=host -e "SPRING_PROFILES_ACTIVE=prod" -it -p8080:8080 redis-contacts:latest



#### Running in `prod` profile with a redis server running on localhost


* This is pretty much the same as before, except we don't need to dockerize a redis instance. We only have to make sure
our localhost redis instance is running.


* If the dockrized redis container is running - stop it

>    * docker stop $(docker ps -a -q --filter ancestor=redis)

* We need to make sure our localhost redis server is running. Start the redis server on `localhost`. Use one of the
following commands:


>    * sudo service redis start
>    * sudo systemctl start redis


* Build the project in `prod` profile, dockerize and run it:

>    * mvn clean spring-boot:build-image -Pprod -DskipTests
>    * docker run --net=host -e "SPRING_PROFILES_ACTIVE=prod" -it -p8080:8080 redis-contacts:latest


* The application is available on <http://localhost:8080>


#### Clean-Up


* We want to stop and remove all containers that ran our application image `redis-contacts:latest`, the application image
and all the left over 'garbage' dangling images:


>    * CTRL+C
>    * docker stop $(docker ps -a -q --filter ancestor=redis)
>    * docker rmi redis-contacts:latest -f
>    * docker rmi $(docker images --filter 'dangling=true' -q --no-trunc)


#### Reverting to `dev` profile


* We use the `-e "SPRING_PROFILES_ACTIVE=prod"` flag in the `docker run` for passing the environment variable
`SPRING_PROFILES_ACTIVE` to docker. We didn't actually need it previously, since we've built the project with the `prod`
profile. We can, however, halt the application, stop the container running the redis server on the production port `6378`
, and rerun the application in profile `dev` (if we want to):


>    * Hit CTR+C to stop the running application
>    * docker stop $(docker ps -a -q --filter ancestor=redis)
>    * docker run --net=host -e "SPRING_PROFILES_ACTIVE=dev" -it -p8080:8080 redis-contacts:latest


* The `--net=host` means the docker container network is now the same as our host so `localhost` in docker is the same as
`localhost` on our hosting machine.


* Since we've built the application using maven in `prod` profile, it will be listening on port `6379`. Running it in
`dev` profile, however, invokes an embedded server instantiation and it will listen on the production port. We don't
really care for which port is uses, since it's an embedded development server, but to avoid network address binding
conflicts, we have to make sure no other redis server is listening on the same port, hence - we shut down the
dockerized redis container. This also means we can keep our `redis-cli -n 0` connection on the same port from the localhost
machine and inspect what the app is doing with redis (for example, count the contact hashes using `SCARD contacts`).


* In all of these cases the application is available on <http://localhost:8080>


#### Clean-Up


* Hit CTR+C to stop the container, remove all exited containers, and remove the `redis-contacts:latest` docker
image, along with every dangling left-over image (no repository, no tag):


>    * CTRL+C
>    * docker stop $(docker ps -a -q --filter ancestor=redis)
>    * docker rmi redis-contacts:latest -f
>    * docker rmi $(docker images --filter 'dangling=true' -q --no-trunc)


#### Method 4: Containerizing with JIB


* The main down side to the previous methods discussed was that we actually needed a docker daemon running so that
spring boot maven plugin can deploy the image to it. With `Jib` we don't even need a docker daemon. We can build the
images and store them on a repository for later `pull` (perhaps from a cloud environment, maybe from a remote host). We
can, also, like before, install an image on docker.


* Jib builds optimized Docker and OCI images for your Java applications without a Docker daemon - and without deep
mastery of Docker best-practices. It is available as plugins for Maven and Gradle and as a Java library.


* Jib is included in this project as a maven plugin. It is possible to configure the plugin to automatically build a
docker image upon project build using `execution` configuration::

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


* To avoid a maven `package` phase clutter however, we'll be calling maven Jib goals manually from command line (for
example: `mvn jib:dockerBuild`).



* To run the application leave your localhost redis instance running - or deploy a dockerized redis image
(e.g. `docker run --name redis-server -p 6379:6379 -v [/path/to]/redis.conf:/etc/redis/redis.conf -d redis`).

* For example:

>   * sudo service redis stop
>   * docker run --name redis-server -p 6379:6379 -v /data/workspaces/jee-2021-06-R/springboot-samples/springboot-rest-redis-contacts/redis.conf:/etc/redis/redis.conf -d redis


#### Running (`prod` profile)


* Using the Jib `dockerBuild` and running the application:

>    * mvn clean package jib:dockerBuild -Pprod -DskipTests
>    * docker run --net=host redis-contacts:latest


#### Running (`dev` profile)


* Alternatively run the application with `dev` profile with an embedded redis server:  Stop any running instance of
redis servers and run the application in `dev` prodile:

>    * sudo service redis stop
>    * docker container stop $(docker container ls -q --filter name=redis)


* Then:


>    * docker run --net=host -e "SPRING_PROFILES_ACTIVE=dev" -it redis-contacts:latest


#### Clean-Up


* We want to stop and remove redis, all containers that ran our application image `redis-contacts:latest`, the application
image and all the left over 'garbage' dangling images:


>    * CTRL+C
>    * docker container rm $(docker ps -a -q --filter ancestor=redis) -f
>    * docker rmi redis-contacts:latest -f
>    * docker rmi $(docker images --filter 'dangling=true' -q --no-trunc)



#### Method 5: Pushing and pulling an image with JIB


* The primary JIB goal `jib:build`, which builds a docker image and uploads it to a docker repository like
[DockerHub](https://hub.docker.com/ "DockerHub"). To use this goal you'll need to open an account and have a
repository. You'll also need to configure the credentials for JIB to authenticate and authorize access to your
repository.


##### Supplying Repository Credentials to JIB


*  For the `jib:build` goal, the easiest way is to provide credentials through maven in .m2/settings.xml (I'm assuming the
use of a [DockerHub](https://hub.docker.com/ "DockerHub") repository in this example):

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

* **Note** however: The recommended way by Google to provide the credentials is to use helper tools, which can store the
 credentials in an encrypted format in the file system. We can use `docker-credential-helpers` instead of storing plain-text
 credentials in settings.xml, which is much safer. See for example [docker-credential-helpers](https://github.com/docker/docker-credential-helpers "docker-credential-helpers").


* The process of installing the `docker-credential-helpers` and configuring an encrypted password for Jib to use, may get
tedious and cumbersome. [Geoff Hudik](https://geoffhudik.com/tech/author/thnk2win/ "Geoff Hudik") has a script for installing and configuring [docker pass credential helper on Ubuntu](https://geoffhudik.com/tech/2020/09/15/docker-pass-credential-helper-on-ubuntu/ "docker pass credential helper on Ubuntu")
, which simplifies the task. The script is included in the `docker-credential-helpers` directory at the root of this project.
You can run it as `root` using `sudo` and it will do the installations and prompt you for input when needed.


##### Pushing and image to a repository:


* To push an image to your repository, configure the jib maven plugin `image` element inside the `to` destination element,
with the repository url prefix and user. For example, this is the configurations for my own DockerHub repository
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

* To push the image to `registry.hub.docker.com` all we have to do is (and wait... this can take some time):


>    * mvn clean package jib:build -DskipTests


* If the process gets stuck for network latency or Jib had lost a connection with the repository, all you have to do
is `mvn jib:build` and it'll pick up from where it had left. Uploading those hundreds of megabytes can take some time.


* There's no cleanup! We don't even need the docker daemon installed on localhost. Once the image is saved to the
repository, anyone (authorized) can install the image on a docker daemon anywhere. I've made a public repository out
of it so no authorization is required other than logging in with docker (`docker login`).


##### Pulling and running an image from a repository:


* The basic pull command is:


>    * docker pull tnsilver/redis-contacts:latest


#### Running


* We don't really need to pull the image though. Once we tell docker to run the `redis-contacts:latest` image and it won't
find it locally, it will attempt to find it in the DockerHub repository. We can simply do with:


>    * docker run --net=host -e "SPRING_PROFILES_ACTIVE=dev" -it tnsilver/redis-contacts:latest


* Since the image resides on my DockerHub repository already, the easiest way for you to install it on your docker is to
pull it and run it.


#### Clean-Up


* Nothing here we haven't seen before:

>    * docker container rm $(docker ps -a -q --filter ancestor=tnsilver/redis-contacts:latest) -f
>    * docker rmi tnsilver/redis-contacts:latest -f
>    * docker rmi $(docker images --filter 'dangling=true' -q --no-trunc)


## Credits

* Many thousands of developers from Sun Microsystems, Oracle, IBM, Microsoft, Google, Netflix, Amazon, and people in the Apache
foundation, Spring IO, Eclipse, IntelliJ, Maven plugin developers, the JQuery team and many others - all deserve credit for
the open source libraries I use.


* Special thanks to [Geoff Hudik](https://geoffhudik.com/tech/author/thnk2win/ "Geoff Hudik") for the [Docker pass Credential Helper on Ubuntu](https://geoffhudik.com/tech/2020/09/15/docker-pass-credential-helper-on-ubuntu/ "Docker pass Credential Helper on Ubuntu")


* and...


![T.N.Silverman](../images/me.jpg?raw=true "T.N.Silverman")

* All java code, property files, client Java Scripts, JSP's and HTML templates created by [T.N. Silverman](https://github.com/tnsilver "About T.N.Silverman")


## License

[Licensed under the Apache License, Version 2.0](LICENSE.md "Apache License")
