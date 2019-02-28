package com.cleverbuilder.cameldemos.components;

import org.apache.camel.Exchange;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.infinispan.InfinispanConstants;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.infinispan.commons.api.BasicCache;
import org.infinispan.commons.api.BasicCacheContainer;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.junit.Before;
import org.junit.Test;

/**
 * Test using a Lucene query in Infinispan using Camel.
 *
 */
public class InfinispanLuceneQuery extends CamelTestSupport {

    protected BasicCacheContainer basicCacheContainer;

    @Override
    @Before
    public void setUp() throws Exception {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        basicCacheContainer = new DefaultCacheManager(builder.build());
        basicCacheContainer.start();
        super.setUp();
    }

    @Override
    public void tearDown() throws Exception {
        basicCacheContainer.stop();
        super.tearDown();
    }

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry registry = super.createRegistry();
        registry.bind("cacheContainer", basicCacheContainer);
        return registry;
    }

    protected BasicCache<Object, Object> currentCache() {
        return basicCacheContainer.getCache();
    }

    protected BasicCache<Object, Object> namedCache(String name) {
        return basicCacheContainer.getCache(name);
    }

    @Test
    public void testLuceneQuery() throws Exception {
        currentCache().put("student1", "Barry Smith");
        currentCache().put("student2", "Selina Scott");

        Exchange response = template.send("direct:start", new DefaultExchange(context));
        System.out.println(response);

    }

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {

        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                        .setHeader(InfinispanConstants.OPERATION, constant(InfinispanConstants.QUERY));
            }
        };
    }
}
