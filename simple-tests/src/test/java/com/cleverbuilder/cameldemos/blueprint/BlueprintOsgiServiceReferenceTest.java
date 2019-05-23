package com.cleverbuilder.cameldemos.blueprint;

import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.blueprint.CamelBlueprintTestSupport;
import org.junit.Test;

public class BlueprintOsgiServiceReferenceTest extends CamelBlueprintTestSupport {

    @Override
    protected String getBundleFilter() {
        // Remove the bundles we don't want to be added into the context, using a filter
        // CamelBlueprintHelper normally just scans the classpath for bundles and adds them all.
        // Removing infinispan because it seems to contain a blueprint XML file inside it, and that was causing the test to hang.
        // We don't want that, do we!
        // I found this out by enabling all DEBUG logs (which my IntelliJ already seems to do - no idea why - possibly because Spring is on the classpath or something)
        return "(&(Bundle-SymbolicName=*)(!(Bundle-SymbolicName=org.infinispan.core)))";
    }

    @EndpointInject(uri = "mock:output")
    MockEndpoint mockOutput;

    @Override
    protected String getBlueprintDescriptor() {
        return "/com/cleverbuilder/cameldemos/blueprint/osgi-reference-test.xml";
    }

    @Test
    public void testHello() throws Exception {
        mockOutput.expectedMessageCount(1);

        template.sendBody("direct:start", "Hiya");

        assertMockEndpointsSatisfied();
    }
}
