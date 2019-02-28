package com.cleverbuilder.cameldemos.spring.transform;

import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.TestSupport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * Created by tdonohue on 19/02/2018.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class SpringTransformTest extends TestSupport {

    @EndpointInject(uri = "mock:csv-json-output")
    MockEndpoint mockCsvJsonOutput;

    @Autowired
    protected ProducerTemplate template;

    @Test
    public void testAlbumTracksCsv() throws InterruptedException {
        String expected = "[{\"Artist\":\"BK\",\"Title\":\"P.O.S. 51\",\"Remix\":\"\",\"Length\":\"5:25\"},{\"Artist\":\"Oxia\",\"Title\":\"Contrast\",\"Remix\":\"\",\"Length\":\"3:03\"},{\"Artist\":\"Exit EEE\",\"Title\":\"Epidemic\",\"Remix\":\"Edison Factor Remix\",\"Length\":\"5:25\"},{\"Artist\":\"Mark Gray\",\"Title\":\"99.9\",\"Remix\":\"\",\"Length\":\"4:46\"}";
        mockCsvJsonOutput.expectedMessageCount(1);

        template.sendBody("direct:start", new File("src/test/data/album_tracks.csv"));

        mockCsvJsonOutput.assertIsSatisfied();

        String output = mockCsvJsonOutput.getExchanges().get(0).getIn().getBody(String.class);
        assertTrue(output.startsWith(expected));
    }

}