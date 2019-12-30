package xyz.tomd.cameldemos.springboot.mongodb.springdata;

import org.springframework.data.mongodb.repository.MongoRepository;
import xyz.tomd.cameldemos.springboot.mongodb.model.CatPojo;

import java.util.List;

// Where CatPojo is the type, and String is the type representing the object's ID
public interface CatsRepository extends MongoRepository<CatPojo, String> {

    List<CatPojo> findAll();

    /*
    The name of this method is important, as it works using Spring magic.
    Spring will look for a property on the CatPojo class named "enemies".
    It will then know how to construct the query to Mongo, even though Enemies is a List.
    */
    List<CatPojo> findByEnemies(String enemy);

}
