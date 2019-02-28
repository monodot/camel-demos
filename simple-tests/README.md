# simple-camel-tests

A place for simple tests of Camel functionality

- Spring-based tests are located in `src/test/java/com/cleverbuilder/cameldemos/spring`. Each test class is annotated with `@ContextConfiguration` and will automatically pick up the corresponding `ClassName-context.xml` XML config file from `src/test/resources/...`.

To run against a specific Camel version:

    mvn clean test -Dtest=YourtestName -Dcamel.version=2.21.0
