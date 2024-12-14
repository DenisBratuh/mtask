package com.example.mtask.repository;

import com.example.mtask.entity.Category;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    @NotNull
    Optional<Category> findById(@NotNull UUID id);

    Optional<Category> findByName(String name);

    @NotNull
    Page<Category> findAll(@NotNull Pageable pageable);
}
