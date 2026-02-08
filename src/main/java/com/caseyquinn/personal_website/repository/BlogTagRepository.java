package com.caseyquinn.personal_website.repository;

import com.caseyquinn.personal_website.entity.BlogTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for BlogTag persistence operations.
 */
@Repository
public interface BlogTagRepository extends JpaRepository<BlogTag, Long> {

    Optional<BlogTag> findByName(String name);

    Optional<BlogTag> findBySlug(String slug);

    boolean existsByName(String name);

    boolean existsBySlug(String slug);

    List<BlogTag> findAllByOrderByUsageCountDesc();

    List<BlogTag> findTop10ByOrderByUsageCountDesc();

    List<BlogTag> findAllByOrderByNameAsc();

    @Modifying
    @Query("UPDATE BlogTag t SET t.usageCount = t.usageCount + 1 WHERE t.id = :id")
    void incrementUsageCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE BlogTag t SET t.usageCount = t.usageCount - 1 WHERE t.id = :id AND t.usageCount > 0")
    void decrementUsageCount(@Param("id") Long id);
}
