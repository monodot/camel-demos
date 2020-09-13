# spring-boot-example-database-tx

An example showing a simple database transaction with Camel.

This demonstrates that database statements can be wrapped in a transaction using the `transacted` command. When executing sequential database statements from Camel, if a later statement fails, then it should cause the entire transaction to be rolled back.

To run:

    $ mvn clean spring-boot:run
    $ mvn clean test

