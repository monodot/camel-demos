# simple-camel-tests

A place for simple tests of Camel functionality. This is where I prototype stuff and write tests.

Spring-based tests are located under `src/test/java/com/cleverbuilder/cameldemos/spring`. Each test class is annotated with `@ContextConfiguration` and will automatically pick up the corresponding XML config file named `ClassName-context.xml` from `src/test/resources/...`.

To run a test against a specific Camel version:

    mvn clean test -Dtest=YourtestName -Dcamel.version=2.21.0
