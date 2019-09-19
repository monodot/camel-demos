package com.cleverbuilder.cameldemos.components;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * Demonstration of streaming a file from S3 back to a hungry client via HTTP.
 *
 * NB: This is a functional test and will attempt to connect to S3 for real, ken? No mocks.
 *
 * To run this test:
 * $ mvn clean test -Dtest=S3ComponentStreamTest -Daws.accesskey=XXXX -Daws.secretkey=YYYY
 * $ curl http://localhost:8111/stream-me-a-file
 */
public class S3ComponentStreamTest extends CamelTestSupport {

    private String accessKey;
    private String secretKey;
    private String bucketName;
    private String objectKey;


    @Override
    @Before
    public void setUp() throws Exception {
        accessKey = System.getProperty("aws.accesskey");
        secretKey = System.getProperty("aws.secretkey");
        bucketName = System.getProperty("aws.bucketname");
        objectKey = System.getProperty("aws.objectkey");

        super.setUp();
    }

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry registry = super.createRegistry();

        AWSCredentials awsCredentials = new
                BasicAWSCredentials(accessKey, secretKey);
        AmazonS3 client = new AmazonS3Client(awsCredentials);
        registry.bind("client", client);

        return registry;
    }

    @Ignore
    @Test
    public void testStreamFileFromS3() throws Exception {
        // Wait until one request is processed (we'll do this from the command line)
        NotifyBuilder notify = new NotifyBuilder(context).whenDone(1).create();

        assertTrue(notify.matches(5, TimeUnit.MINUTES));
    }


    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("jetty:http://localhost:8111/stream-me-my-file")
                        .log("Getting a file")
                        .pollEnrich()
                        .simple("aws-s3://" + bucketName + "" +
                                "?amazonS3Client=#client" +
                                "&fileName=" + objectKey +
                                "&deleteAfterRead=false")
                        .log("Retrieved it!");


            }
        };
    }
}
