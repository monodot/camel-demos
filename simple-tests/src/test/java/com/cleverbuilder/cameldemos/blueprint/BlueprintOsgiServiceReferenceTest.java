package com.cleverbuilder.cameldemos.blueprint;

import com.cleverbuilder.cameldemos.model.Person;
import com.cleverbuilder.cameldemos.model.PersonImpl;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.blueprint.CamelBlueprintTestSupport;
import org.apache.camel.util.KeyValueHolder;
import org.junit.Test;

import java.util.Dictionary;
import java.util.Map;

public class BlueprintOsgiServiceReferenceTest extends CamelBlueprintTestSupport {

    private Person person = new PersonImpl("Dave Lee", "Travis");

    @Override
    protected String getBundleFilter() {
        // Remove the bundles we don't want to be added into the context, using a filter
        // CamelBlueprintHelper normally just scans the classpath for bundles and adds them all.
        // Removing infinispan because it seems to contain a blueprint XML file inside it, and that was causing the test to hang.
        // Also removing CXF because it complains about there being no HTTP server available
        // We don't want that, do we....
        // I found this out by enabling all DEBUG logs - check logback config XML for this.
        return "(&(Bundle-SymbolicName=*)" +
                "(!(Bundle-SymbolicName=org.infinispan.core))" +
                "(!(Bundle-SymbolicName=org.apache.cxf.cxf-rt-transports-http))" +
                ")";
    }

    @Override
    protected String getBlueprintDescriptor() {
        return "/com/cleverbuilder/cameldemos/blueprint/osgi-reference-test.xml";
    }

    /**
     * Add the required service (a Person) into the Blueprint context
     */
    @Override
    protected void addServicesOnStartup(Map<String, KeyValueHolder<Object, Dictionary>> services) {
        // Give the type (interface) of the object to register,
        // and pass the object itself
        services.put(Person.class.getName(), asService(person, null));
    }

    @Test
    public void testContextOk() throws Exception {
        // Just check that the Camel Context started correctly
        assertTrue(context.getStatus().isStarted());
    }


    @Test
    public void testHello() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:output");

        mock.expectedMessageCount(1);
        mock.message(0).body().isInstanceOf(Person.class);

        template.sendBody("direct:start", "Hiya");

        assertMockEndpointsSatisfied();
    }
}
