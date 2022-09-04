# simple-camel-tests

A place for simple tests of Camel functionality. This is where I prototype stuff and write tests.

Spring-based tests are located under `src/test/java/com/cleverbuilder/cameldemos/spring`. Each test class is annotated with `@ContextConfiguration` and will automatically pick up the corresponding XML config file named `ClassName-context.xml` from `src/test/resources/...`.

Some of the tests rely on resources, so you should ensure that you first compile/install the application, e.g.:

    mvn clean install -DskipTests

Then to run a test against a specific Camel version:

    mvn clean test -Dtest=YourtestName -Dcamel.version=2.21.0

## List of scenarios

Here's a (non-exhaustive!) list of the scenarios included in this part of the repo, primarily for the benefit of people searching the web. Scenarios are usually where I do little POCs to prove something, or not.

- **CxfCustomNamespacePrefixTest** - This shows how to customise CXF to use specific prefixes when referring to namespaces. This was born out of a requirement where a legacy web service client was not able to understand unqualified values in XML attributes. CXF will try to set a default namespace where it can. If legacy clients don't fully understand this, then CXF behaviour can be customised by creating a CXF endpoint manually and setting namespaces. This scenario shows how.

## Other things

To generate client classes from WSDL:

   mvn org.apache.cxf:cxf-codegen-plugin:wsdl2java