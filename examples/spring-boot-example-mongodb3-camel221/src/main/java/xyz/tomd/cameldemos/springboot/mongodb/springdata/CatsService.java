package xyz.tomd.cameldemos.springboot.mongodb.springdata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.tomd.cameldemos.springboot.mongodb.model.CatPojo;

@Component
public class CatsService {

    @Autowired
    CatsRepository catsRepository;

    public Iterable<CatPojo> findAll() {
        return catsRepository.findAll();
    }

    public CatPojo save(CatPojo cat) { return catsRepository.save(cat); }

    public Iterable<CatPojo> findByEnemy(String enemy) { return catsRepository.findByEnemies(enemy); }

}
