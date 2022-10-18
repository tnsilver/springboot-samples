SpringBoot / JQuery (No node.js) Consume and Produce Protobuf
=============================================================

This project demonstrates using protobuf with a `SpringBoot` server and a `JQuery` JavaScript client.
The server produces protobuf messages, to be consumed in a JavaScript client utilizing JQuery [DataTables](https://datatables.net/ "DataTables").
The DataTables in turn, send protobuf messages via `ajax` to be consumed by the `SpringBoot` server.

### Running the application

*	cd to the project root directory `springboot-js-protobuf-example`


*   `mvn clean package spring-boot:run -DskipTests`


#### Protobuf JavaScript Dependencies used in this project:

*	[google-closure-library](https://github.com/google/closure-library/ "google-closure-library") - copied directory `closure` into `${basedir}/src/main/resources/static/js/google-closure-library`


*	[protobuf.js/node_modules/google-protobuf](https://github.com/protobufjs/protobuf.js "google-protobuf node.js module") - copied directory `protobuf.js/node_modules/google-protobuf/` into `${basedir}/src/main/resources/static/js/` after running `npm install` from root directory of the `protobuf.js` project.


*   [jquery.binarytransport.js](https://github.com/henrya/js-jquery "jquery.binarytransport.js") script copied to `${basedir}/src/main/resources/static/js/jquery.binarytransport.js`. This is the script that allows jQuery ajax to transport binary data type requests without corrupting the byte stream in an attempt to encode it to utf-8.

#### Generate Prorobuf Java Resources

From the root of the project - generated with:

*   `protoc -I=.  --java_out=src/main/java  src/main/resources/proto/training.proto`

#### Generate Prorobuf Java Resources with server side validation extension

* For protobuf validation in Java using `protoc-gen-validate` see the required `protoc` plugin installation on [https://github.com/bufbuild/protoc-gen-validate](https://github.com/bufbuild/protoc-gen-validate "protoc-gen-validate")


* Note that the documented command `go get -d github.com/envoyproxy/protoc-gen-validate` is deprecated. Instead use `go install github.com/envoyproxy/protoc-gen-validate@latest` and `go install google.golang.org/protobuf/cmd/protoc-gen-go@latest` from the root directories of the downloaded (cloned) projects.


* you must have `go` installed (typically to `/usr/local/go`), define `$GOPATH` (i.e. $HOME/go) and have `protoc-gen-go` and `protoc-gen-validate` in `$GOPATH/bin` and on the $PATH.


* I have defined the following in ~/.bashrc:

	 export GOROOT=/usr/local/go
	 export GOPATH=$HOME/go
	 export GOBIN=$GOPATH/bin
	 export PATH=$PATH:$GOROOT:$GOPATH:$GOBIN

* for validation rules reference see [https://github.com/bufbuild/protoc-gen-validate](https://github.com/bufbuild/protoc-gen-validate "protoc-gen-validate")


* Then, to generate the required Java validation class, use:

*   `protoc -I . -I ${GOPATH}/bin -I ${GOPATH}/pkg/mod/github.com/envoyproxy/protoc-gen-validate@v0.6.13/ --java_out=src/main/java --validate_out="lang=java:src/main/java" src/main/resources/proto/training.proto`

#### Generate Prorobuf JavaScript resources

From the root of the project - generated with:

* To create single lib.js file for all protos:

*	`protoc -I=. -I ${GOPATH}/bin -I ${GOPATH}/pkg/mod/github.com/envoyproxy/protoc-gen-validate@v0.6.13/ --js_out=library=src/main/resources/static/js/training/any,binary:. src/main/resources/proto/any.proto`


* Then:


*   `protoc -I=. -I ${GOPATH}/bin -I ${GOPATH}/pkg/mod/github.com/envoyproxy/protoc-gen-validate@v0.6.13/ --js_out=library=src/main/resources/static/js/training/training_lib,binary:. src/main/resources/proto/training.proto`


* To create a .js file for each proto:


* `protoc -I=. -I ${GOPATH}/bin -I ${GOPATH}/pkg/mod/github.com/envoyproxy/protoc-gen-validate@v0.6.13/ --js_out=import_style=closure,binary:src/main/resources/static/js/training  src/main/resources/proto/training.proto`




CommonJS style is not working:

* `protoc -I=. --js_out=import_style=commonjs,binary:src/main/resources/static/js/protobuf  src/main/resources/proto/training.proto`

Using [protobuf.js/cli/bin/pbjs](https://github.com/protobufjs/protobuf.js "pbjs"):

* `./pbjs -t static-module /data/workspaces/eclipse-jee-2022-09-R/springboot-samples/springboot-js-protobuf-example/src/main/resources/proto/training.proto > /data/workspaces/eclipse-jee-2022-09-R/springboot-samples/springboot-js-protobuf-example/src/main/resources/static/js/protobuf/training_pbjs.js`


#### Test Curl Commands

Get course

* `curl localhost:8080/courses/1`

Save a course:

* `curl localhost:8080/courses/2 | tee proto.dat | base64 > proto.dat`

* `curl -vX POST http://localhost:8080/courses -H "Content-Type: application/x-protobuf" -d @proto.dat`
