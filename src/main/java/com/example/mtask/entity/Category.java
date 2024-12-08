package com.example.mtask.entity;

import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;
//    @OneToMany(mappedBy = "category")
//    private List<Product> cityList;

@Entity
@Table(name = "category")
public class Category {

    public Category(UUID id, String name, String logoUrl) {
        this.id = id;
        this.name = name;
        this.logoUrl = logoUrl;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name")
    private String name;

    //TODO column
    private String logoUrl;

    @OneToMany(mappedBy = "category")
    private List<Product> products;

    public Category() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public List<Product> getProducts() {
        return products;
    }

}
