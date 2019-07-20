# spring-boot-example-file-upload

An example showing file upload with Camel:

- Exposes a REST service using the _servlet_ component
- Adds OpenAPI (Swagger) definition
- Configures the servlet context root to be `/services`

To run and test:

    $ mvn clean spring-boot:run
    $ curl -v -X PUT \
        -H "filename: myfile.txt" \
        -H "Content-Type: text/plain" \
        -d "hello12345" \
        http://localhost:8080/services/upload

