package xyz.tomd.cameldemos.springboot.mongodb.camel;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mongodb.MongoDbConstants;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.stereotype.Component;
import xyz.tomd.cameldemos.springboot.mongodb.model.CatPojo;

import java.util.ArrayList;
import java.util.List;

@Component
public class CamelMongoComponentRouteBuilder extends RouteBuilder {

    public void configure() throws Exception {
        // GET ALL THE LOVELY CATS
        from("direct:getAllUsingCamelComponent")
                // Will return an array of Document objects
                .to("mongodb3:mymongoclient" +
                        "?database={{mongo.database}}" +
                        "&collection={{mongo.collection}}" +
                        "&operation=findAll")
                .process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        // Loop over the Document array
                        Document[] documents = exchange.getMessage().getBody(Document[].class);

                        List<CatPojo> cats = new ArrayList<CatPojo>();

                        // Convert each Document to a Cat
                        for (Document document : documents) {
                            CatPojo cat = new CatPojo();
                            cat.setName(document.getString("name"));
                            cat.setCutenessScore(document.getInteger("cutenessScore"));
                            //noinspection unchecked
                            cat.setEnemies(document.get("enemies", List.class));
                            cat.setFurColour(document.getString("furColour"));

                            cats.add(cat);
                        }

                        // Return the cats
                        exchange.getMessage().setBody(cats);
                    }
                })
                .log("Body is: ${body}");

        // INSERT A NEW CAT
        // This will automatically insert our POJO
        // because Camel just marshals it to JSON and Mongo Java driver likes that.
        from("direct:insertUsingCamelComponent")
                .to("mongodb3:mymongoclient" +
                        "?database={{mongo.database}}" +
                        "&collection={{mongo.collection}}" +
                        "&operation=insert");

        // UPDATE AN EXISTING CAT with a POJO
        // See this page from the Mongo docs:
        // https://docs.mongodb.com/v3.4/tutorial/update-documents/
        // (Camel 2.21.0 mongodb3 component == MongoDB driver 3.4)
        from("direct:updateUsingCamelComponent")
                .process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        // Get the body as a CatPojo so we can extract stuff from it
                        CatPojo cat = exchange.getMessage().getBody(CatPojo.class);

                        // Create our update criteria as a Bson object
                        // To facilitate creating filter objects, Java driver provides the Filters
                        // helper:
                        // com.mongodb.client.model.Filters\
                        // Here we're filtering on both name AND owner
                        Bson filter = Filters.and(
                                Filters.eq("name", cat.getName()),
                                Filters.eq("owner", cat.getOwner()));

                        // When we put the criteria in a Camel header named 'CamelMongoDbCriteria',
                        // Camel will pick it up, and use it as the criteria for the update operation
                        exchange.getMessage().setHeader(MongoDbConstants.CRITERIA,
                                filter);

                        // For updates, we can't just use a POJO with mongo driver < 3.6 (sad face)
                        // Instead we need to create a Bson object which represents the fields
                        // that we want to update. See:
                        // https://mongodb.github.io/mongo-java-driver/3.4/driver/tutorials/perform-write-operations/#update-a-single-document
                        // https://mongodb.github.io/mongo-java-driver/3.4/javadoc/com/mongodb/client/model/Updates.html
                        Bson update = Updates.combine(
                                Updates.set("furColour", cat.getFurColour()),
                                Updates.set("enemies", cat.getEnemies()),
                                Updates.set("cutenessScore", cat.getCutenessScore())
                        );

                        exchange.getMessage().setBody(update);
                    }
                })
                .to("mongodb3:mymongoclient" +
                        "?database={{mongo.database}}" +
                        "&collection={{mongo.collection}}" +
                        "&operation=update")
                .split().body().process(new Processor() {
            public void process(Exchange exchange) throws Exception {

            }
        }).end();
    }

}
