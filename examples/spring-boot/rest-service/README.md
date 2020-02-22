# rest-service

![Apache Camel][camelver]

This is a demo of how to create a simple REST service in Apache Camel using the REST DSL, with Spring Boot.

This is a Spring Boot application. It uses Camel's _servlet_ component to provide the REST service, which runs in the embedded web server, which is running on port 8080.

## Good to know!

Here are some notable features about this demo, to help you understand how it works:

- We add `camel-servlet-starter` as a dependency, which tells Camel to automatically configure a Servlet that will 'host' our RESTful API.

- Since we're using Spring Boot 2.x, we go with the opinionated defaults - in this case, the default servlet container for Spring Boot is Tomcat. So we'll be deploying into an embedded Tomcat server.

- The context path (the HTTP URL which will be mapped to the Servlet) can be changed by setting the property `camel.component.servlet.mapping.context-path` in `application.properties`. In this example, we're mapping to `/services/`.

- The RESTful API is defined in the class `RestDslRouteBuilder`
    - There is a single GET operation, which returns the string "Hello, world!"
    - There is a single POST operation, which takes a _name_ in a JSON object, and then returns that name in the output.

- Camel will marshal (convert) between JSON and Java automatically. This is because we have defined Java classes (POJOs) for our Request and Response types, and they are specified in the REST DSL configuration (as `type()` and `outType()`).

## The example

The code is in `RestDslRouteBuilder` and looks like this:

```java
// This section is required - it tells Camel how to configure the REST service
restConfiguration()
    // Use the 'servlet' component.
    // This tells Camel to create and use a Servlet to 'host' the RESTful API.
    // Since we're using Spring Boot, the default servlet container is Tomcat.
    .component("servlet")

    // Allow Camel to try to marshal/unmarshal between Java objects and JSON
    .bindingMode(RestBindingMode.auto);


// Now define the REST API (POST, GET, etc.)
rest()
    .path("/api") // This makes the API available at http://host:port/$CONTEXT_ROOT/api

    .consumes("application/json")
    .produces("application/json")

    // HTTP: GET /api
    .get()
        .outType(ResponseType.class) // Setting the response type enables Camel to marshal the response to JSON
        .to("bean:getBean") // This will invoke the Spring bean 'getBean'

    // HTTP: POST /api
    .post()
        .type(PostRequestType.class) // Setting the request type enables Camel to unmarshal the request to a Java object
        .outType(ResponseType.class) // Setting the response type enables Camel to marshal the response to JSON
        .to("bean:postBean");
```

## To run

To run the app:

    mvn clean spring-boot:run

Then, to test the REST service's GET operation - see how it returns a JSON object:

    $ curl http://localhost:8080/services/api/
    {"message":"Hello, world!"}

And to test the REST service's POST operation - see how it returns a JSON object:

    $ curl --request POST \
        --data "{ \"name\": \"Jeff Mills\" }" \
        --header "Content-Type: application/json" \
        http://localhost:8080/services/api/
    {"message":"Thanks for your post, Jeff Mills!"}

[camelver]: https://img.shields.io/badge/dynamic/xml?label=Tested%20with%20Apache%20Camel&query=%2F%2A%5Blocal-name%28%29%3D%27project%27%5D%2F%2A%5Blocal-name%28%29%3D%27properties%27%5D%2F%2A%5Blocal-name%28%29%3D%27camel.version%27%5D&url=https%3A%2F%2Fraw.githubusercontent.com%2Fmonodot%2Fcamel-demos%2Fmaster%2Fparent%2Fpom.xml&color=orange
