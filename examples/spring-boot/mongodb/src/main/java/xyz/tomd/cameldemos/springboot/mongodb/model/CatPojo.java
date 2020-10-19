package xyz.tomd.cameldemos.springboot.mongodb.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "verycutecats")
public class CatPojo {

    @Id
    private String id;

    private String name;
    private String owner;
    private String furColour;
    private double cutenessScore; // cuteness goes from zero to 100CUUUUUTE
    private List<String> enemies;

    public CatPojo() {
    }

    public CatPojo(String name, String owner, String furColour, double cutenessScore, List<String> enemies) {
        this.name = name;
        this.owner = owner;
        this.furColour = furColour;
        this.cutenessScore = cutenessScore;
        this.enemies = enemies;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFurColour() {
        return furColour;
    }

    public void setFurColour(String furColour) {
        this.furColour = furColour;
    }

    public double getCutenessScore() {
        return cutenessScore;
    }

    public void setCutenessScore(double cutenessScore) {
        this.cutenessScore = cutenessScore;
    }

    public List<String> getEnemies() {
        return enemies;
    }

    public void setEnemies(List<String> enemies) {
        this.enemies = enemies;
    }

    @Override
    public String toString() {
        return "CatPojo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", owner='" + owner + '\'' +
                '}';
    }
}
