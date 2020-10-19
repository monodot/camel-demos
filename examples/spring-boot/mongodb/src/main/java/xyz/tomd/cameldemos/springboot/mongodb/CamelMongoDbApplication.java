package xyz.tomd.cameldemos.springboot.mongodb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import xyz.tomd.cameldemos.springboot.mongodb.configuration.MongoConfiguration;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@SpringBootApplication
public class CamelMongoDbApplication {

    public static void main(String[] args) {
        SpringApplication.run(CamelMongoDbApplication.class, args);
    }

    @Bean(name = "mymongoclient")
    MongoClient mongoClient(MongoConfiguration properties) {
        // Camel 2.21.0 uses mongo driver 3.4 which does NOT include POJO support.
        // From 3.6 onwards, set up a CodecRegistry - this allows us to use POJOs with the Mongo driver
        // There is no MongoClient(host, port, MongoClientOptions) constructor, so we use MongoClient(ServerAddress addr, MongoClientOptions options)
        MongoClient client = MongoClients.create(
                "mongodb://" + properties.getHost()
                        + ":" + properties.getPort());
//        MongoClient client = new MongoClient(properties.getHost(), properties.getPort());
        return client;
    }

    @Bean
    MongoTemplate mongoTemplate(MongoClient client, MongoConfiguration properties) {
        // Creating a MongoTemplate explicitly to set which Mongo database to use.
        // We can do this implicitly by just setting the spring.data.mongodb.* properties
        MongoTemplate mongoTemplate = new MongoTemplate(client, properties.getDatabase());
        return mongoTemplate;
    }

}
