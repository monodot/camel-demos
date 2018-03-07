# rest-dsl

Demos a simple REST service in Apache Camel using the REST DSL.

This is a Spring Boot application. It uses Camel's _restlet_ component to provide the REST service, which runs on localhost, port 8080.

There is:

- A single GET operation which returns the string "Hello, world!"
- A single POST operation which takes a _name_ in a JSON object, and then returns that name in the output.

To run the app:

    mvn clean spring-boot:run

Then, to test the REST service's GET operation:

    $ curl http://localhost:8080/api/
    "Hello, world!"

And to test the REST service's POST operation:

    $ curl --request POST \
        --data "{ \"name\": \"Jeff Mills\" }" \
        --header "Content-Type: application/json"
        http://localhost:8080/api/
    "Thanks for your post, Jeff Mills!"
