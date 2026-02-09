package com.caseyquinn.personal_website.repository;

import com.caseyquinn.personal_website.entity.BlogCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for BlogCategory persistence operations.
 */
@Repository
public interface BlogCategoryRepository extends JpaRepository<BlogCategory, Long> {

    Optional<BlogCategory> findByName(String name);

    Optional<BlogCategory> findBySlug(String slug);

    boolean existsByName(String name);

    boolean existsBySlug(String slug);

    List<BlogCategory> findAllByOrderByNameAsc();
}
