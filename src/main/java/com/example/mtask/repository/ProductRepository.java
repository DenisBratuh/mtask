package com.example.mtask.repository;

import com.example.mtask.entity.Product;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByCategoryName(String name);

    List<Product> findByCategoryId(UUID categoryId);

    @Query("SELECT DISTINCT p.name FROM Product p")
    List<String> findDistinctProductNames();

    @NotNull
    Page<Product> findAll(@NotNull Pageable pageable);
}
