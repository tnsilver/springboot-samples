# Contacts: Spring Boot RESTful CRUD Web-App Demo with MongoDB

##### Basic commands to run and test the application

* `cd` into the project's root directory from a command line shell


* type: `./mvnw clean package spring-boot:run -DskipTests` to run the application


* or type: `./mvnw clean test` to test the application


* or type: `./mvnw clean test -Dtest=ApplicationIT -Ptest` to run the application in `test` profile (runs for 5 minutes and exits)


* browse to [http://localhost:8080](http://localhost:8080 "http://localhost:8080") to use the application


* see [Testing and running the applications from a shell window](../README.md#testing-and-running-the-applications-from-a-shell-window) for details


![Contacts List JPA](../images/mongo/contacts-mongo.jpg?raw=true "Contacts List JPA")


![Add JPA Contact](../images/mongo/contacts-mongo-add.jpg?raw=true "Add JPA Contact")


![About JPA Contact](../images/mongo/contacts-mongo-about.jpg?raw=true "About JPA Contact")



## What is in here?

A SpringBoot CRUD (create, retrieve, update delete) web application master/details
to manage a list of contacts with **MongoDB** serving as the persistence storage.

## Table of contents

* [Installing, testing and running](#installing-testing-and-running)
* [Executive Summary: Running the Application](#executive-summary)
* [Testing the REST API with curl](#testing-the-rest-api-with-curl)
* [Exporting the host address to a local environment variable](#exporting-the-host-address-to-a-local-environment-variable)
* [Performing REST queries with curl](#performing-rest-queries-with-curl)
* [Performing data manipulation with curl](#performing-data-manipulation-with-curl)
* [Credits](#credits)
* [License](#license)


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
This exposes the entire available public REST API:


> * curl -v -G $HOST/api/profile/contacts | jq .


* next, query the paged contacts. Notice the `application/hal+json` response type and the paged response:

> * curl -v  -G -k "$HOST/api/contacts?size=100" | jq .

* now query the search functions. Notice the `application/hal+json` response type
and the publicly available search functions:

> * curl -X "GET" -v -H "Content-Type: application/json" -H "Cache-Control: no-cache" -k "$HOST/api/contacts/search" | jq .

* You can use the search functions to execute a query by example and find contacts with, for example,
 having `Simpsons` for their last name. Notice the **pagination parameters** at the end of the `curl` URL,
 and the pagination data at the end of the `application/hal+json` response.

> * curl -X "GET" -v -H "Content-Type: application/json" -H "Cache-Control: no-cache" -k "$HOST/api/contacts/search/byParams?ssn=&firstName=&lastName=Simpson&married=false&children=&page=0&size=5&sort=id,ASC" | jq .

## Performing data manipulation with curl

Since Spring Security is authorizing requests and does not allow anonymous access to data manipulation operations via the appropriate `POST`, `PUT`, `PATCH` or `DELETE` REST `http methods`, from now on all `curl` commands must include the `-u user:password` basic authentication data.


* The following requires disabling `csrf protection` in `SecurityConfig` which does that for the `test` and `dev` profiles.


* We can **INSERT** (create or save) a new contact with `curl` and the `POST` http method. Notice the
json (`-d`) data format and the http headers, each with the `-H` flag. The response status is `201`
(created) and the response media type is `application/json`.

>
```
curl  -v -u user:password -X POST $HOST/api/contacts \
-H "Content-Type: application/json" \
-H "Accept: application/json" \
-H "Cache-Control: no-cache" \
-d \
'{"ssn":{"ssn":"987-56-4231"},
  "firstName":"Lucky",
  "lastName":"Luciano",
  "birthDate":"1897-11-24",
  "married": true,
  "children":"0"}' | jq .
```


* We can **UPDATE** (save) a new contact with `curl` and the `PUT` http method.
The response status is `200` (OK) and the response media type is `application/json`.

>
```
curl  -v -u user:password -X PUT $HOST/api/contacts/35 \
-H "Content-Type: application/json" \
-H "Accept: application/json" \
-H "Cache-Control: no-cache" \
-d \
'{"ssn":{"ssn":"987-56-4231"},
  "firstName":"Lucky",
  "lastName":"Luke",
  "birthDate":"1946-11-24",
  "married": true,
  "children":"0"}' | jq .
```

* We can also **UPDATE** (save changes of) a new contact with `curl` and the `PATCH` http method.
Here we can supply only the delta of changes (last name and birth date in this case). The
response status is `200` (OK) and the response media type is `application/json`.

>
```
curl  -v -u user:password -X PATCH $HOST/api/contacts/35 \
-H "Content-Type: application/json" \
-H "Accept: application/json" \
-H "Cache-Control: no-cache" \
-d \
'{"lastName":"Luciano", "birthDate": "1897-11-24"}' | jq .
```

* Finally... we can delete the contact with `curl` and the `DELETE` http method. There's no response body after deletion,
but the response status is `204` (deleted)

>
```
curl -v -u user:password -X DELETE $HOST/api/contacts/35 \
-H "Content-Type: application/json" \
-H "Cache-Control: no-cache"
```

* if we try that again, the response status is `404` (not found), which is
expected as we've just deleted the contact.

>
```
curl -v -u user:password -X DELETE $HOST/api/contacts/35
```

* If we need to restore the deleted contact, we use `curl` to `POST` it again. Notice the id of
the contact is now different than before. Again, the response status is `201` (created) and the response media
type is `application/json`.

>
```
curl  -v -u user:password -X POST $HOST/api/contacts \
-H "Content-Type: application/json" \
-H "Accept: application/json" \
-H "Cache-Control: no-cache" \
-d \
'{"ssn":{"ssn":"987-56-4231"},
  "firstName":"Lucky",
  "lastName":"Luciano",
  "birthDate":"1897-11-24",
  "married": true,
  "children":"0"}' | jq .
```

* We can query all the contacts and see the new id. Notice the gap between the next to
last contact (`id`: 34) and our new contact (`id`: 36). The gap was created when we initially
deleted the contact. The identity property of the contacts is unique and non-restoreable
in this application.

>
```
curl -v  -k "$HOST/api/contacts?size=100" | jq .
```

## Credits

* Many thousands of developers from Sun Microsystems, Oracle, IBM, Microsoft, Google, Netflix, Amazon, and people in the Apache
foundation, Spring IO, Eclipse, IntelliJ, Maven plugin developers, the JQuery team and many others - all deserve credit for
the open source libraries I use.

#### but credits for this app go to:

![T.N.Silverman](../images/me.jpg?raw=true "T.N.Silverman")

* All java code, property files, Java Scripts, JSP's and HTML templates created by [T.N. Silverman](https://github.com/tnsilver "About T.N.Silverman")


## License

[Licensed under the Apache License, Version 2.0](LICENSE.md "Apache License")
