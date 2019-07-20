# spring-boot-example-mongodb3-camel221

An example of using the **camel-mongodb3** component in Camel 2.21 to connect to a MongoDB instance. Also shows implementing most of the equivalent functionality using Spring Data MongoDB.

Findings and thoughts:

- The original **camel-mongodb** component is [deprecated from Camel 2.23 onwards][deprecated].
- **camel-mongodb3** in Camel 2.21 uses [Mongo Java Driver 3.4.3][mjd343] and doesn't yet support the replace operation.
- Support for POJOs arrived in version 3.5.0 of the Mongo Java Driver
- Alternatives:
    - Build up BSON documents manually.
    - Use another mapping tool like [Morphia][morphia]
    - Use Spring Data and just implement as a Bean - Spring Data will handle the mapping to/from POJOs

To test:

    $ docker run --rm --name mongo -d -p 27017:27017 mongo:3.4
    $ mvn clean test -Dtest=CamelMongoDbApplicationIT

[deprecated]: https://issues.apache.org/jira/browse/CAMEL-12611
[mjd343]: https://mongodb.github.io/mongo-java-driver/3.4/
[morphia]: https://morphia.dev
