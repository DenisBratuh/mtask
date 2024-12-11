package com.example.mtask.entity;

import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Entity
@Table(name = "product")
public class Product {

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "logo_url")
    private String logoUrl;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

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

    @NotNull
    public Category getCategory() {
        return category;
    }

    public void setCategory(@NotNull Category category) {
        this.category = category;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }
}

