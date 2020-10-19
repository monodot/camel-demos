package xyz.tomd.cameldemos.springboot.mongodb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.bson.BsonInt32;
import org.bson.Document;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import xyz.tomd.cameldemos.springboot.mongodb.model.CatPojo;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * This test will not run by default when running `mvn clean test`.
 * It needs a Mongo instance running. See the README for details.
 */
@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = CamelMongoDbApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CamelMongoDbApplicationIT {

    private static final Logger LOG = LoggerFactory.getLogger(CamelMongoDbApplicationIT.class);

    @Autowired
    private CamelContext camelContext;

    @Autowired
    private ProducerTemplate template;

    @Autowired
    private MongoClient mongoClient;

    @Value("${mongo.database}")
    private String mongoDatabaseName;

    @Value("${mongo.collection}")
    private String mongoCollectionName;

    private MongoCollection<Document> catsCollection;

    @Before
    public void setUp() throws Exception {
        // Drop the collection before we start each test
        MongoDatabase db = mongoClient.getDatabase(mongoDatabaseName);
        catsCollection = db.getCollection(mongoCollectionName);
        catsCollection.drop();
        LOG.info("**** Cats collection dropped - size is now: " + catsCollection.countDocuments());
    }

    @Test
    public void shouldGetThemAllCamelComponent() throws Exception {
        // Manually add one Cat into the collection
        Document cat = new Document("name", "Willybur")
                .append("furColour", "ginger")
                // Mongo stores numbers as floats by default, so if we want an Int we need to be explicit.
                .append("cutenessScore", new BsonInt32(78))
                .append("enemies", Arrays.asList("Brian", "Snowdrop"));
        catsCollection.insertOne(cat);

        // Hit the endpoint with an empty body and get the response
        List response = template.requestBody("direct:getAllUsingCamelComponent",
                null, List.class);

        // Response should contain one CatPojo -- our friend Willybur
        assertEquals("Willybur", ((CatPojo) response.get(0)).getName());
    }

    /**
     * Test whether we can get a list of cats using Spring Data with Camel
     * @throws Exception
     */
    @Test
    public void shouldGetThemAllSpringData() throws Exception {
        // Manually add one Cat into the collection
        Document cat = new Document("name", "Fenella")
                .append("furColour", "ginger")
                // Mongo stores numbers as floats by default, so if we want an Int we need to be explicit.
                .append("cutenessScore", new BsonInt32(78))
                .append("enemies", Arrays.asList("Brian", "Snowdrop"));
        catsCollection.insertOne(cat);

        // Hit the endpoint with an empty body and get the response
        List response = template.requestBody("direct:getAllUsingSpringData",
                null, List.class);

        // Response should contain one CatPojo -- our friend Fenella
        assertEquals(78, ((CatPojo) response.get(0)).getCutenessScore(), 0);
    }

    @Test
    public void shouldGetCatsByEnemySpringData() throws Exception {
        // Manually add one Cat into the collection
        Document cat = new Document("name", "Fenella")
                .append("furColour", "ginger")
                // Mongo stores numbers as floats by default, so if we want an Int we need to be explicit.
                .append("cutenessScore", new BsonInt32(78))
                .append("enemies", Arrays.asList("Brian", "Snowdrop"));
        catsCollection.insertOne(cat);

        // Manually add a Cat with different enemies
        Document cat2 = new Document("name", "Bruce")
                .append("furColour", "white")
                // Mongo stores numbers as floats by default, so if we want an Int we need to be explicit.
                .append("cutenessScore", new BsonInt32(3))
                .append("enemies", Arrays.asList("Bobby", "Bertie"));
        catsCollection.insertOne(cat2);

        // Hit the endpoint to search for all cats with enemy Snowdrop
        // Under the hood this will create a query:
        // "Created query Query: { "enemies" : "Snowdrop"}, Fields: null, Sort: null"
        List response = template.requestBodyAndHeader("direct:getByOwnerUsingSpringData",
                null,
                "enemy", "Snowdrop",
                List.class);

        // Response should contain one CatPojo -- our friend Fenella
        assertEquals("Fenella", ((CatPojo) response.get(0)).getName());
    }


    @Test
    public void shouldInsertCamelComponent() throws Exception {
        // Call the insert endpoint with a POJO. Camel will marshal this to JSON and send to Mongo.
        template.sendBody("direct:insertUsingCamelComponent",
                new CatPojo("Snowdrop", "Bryan May",
                        "ginger", 78,
                        Arrays.asList("Willybur", "Frank")));

        // Assert that Snowdrop was inserted
        Document cat = catsCollection.find(Filters.eq("name", "Snowdrop")).first();
        assertEquals("Bryan May", cat.getString("owner"));
    }

    @Test
    public void shouldInsertSpringData() throws Exception {
        // Call the insert endpoint with a POJO. Camel will marshal this to JSON and send to Mongo.
        template.sendBody("direct:insertUsingSpringData",
                new CatPojo("Springdrop", "John Deacon",
                        "ginger", 78,
                        Arrays.asList("Willybur", "Frank")));

        // Assert that Snowdrop was inserted
        Document cat = catsCollection.find(Filters.eq("name", "Springdrop")).first();
        assertEquals("John Deacon", cat.getString("owner"));
    }


    @Test
    public void shouldNotCreateACatCamelComponent() throws Exception {
        // If a cat named Tabitha does not exist, no record will be updated.
        template.sendBody("direct:updateUsingCamelComponent",
                new CatPojo("Tabitha", "William Shakespeare",
                        "black", 45,
                        Arrays.asList("Susie", "Wendy")));

        // Check that we have NOT created a cat called Tabitha (because this was an Update only)
        Document cat = catsCollection.find(Filters.eq("name", "Tabitha")).first();
        assertNull(cat);
    }




}