package com.caseyquinn.personal_website.repository;

import com.caseyquinn.personal_website.entity.BlogPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for BlogPost persistence operations.
 */
@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {

    Optional<BlogPost> findByTitle(String title);

    Optional<BlogPost> findBySlug(String slug);

    boolean existsByTitle(String title);

    boolean existsBySlug(String slug);

    List<BlogPost> findByPublishedTrueOrderByPublishedAtDesc();

    Page<BlogPost> findByPublishedTrue(Pageable pageable);

    List<BlogPost> findAllByOrderByCreatedAtDesc();

    @Query("SELECT p FROM BlogPost p JOIN p.categories c WHERE c.id = :categoryId")
    List<BlogPost> findByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT p FROM BlogPost p JOIN p.categories c WHERE c.slug = :slug AND p.published = true ORDER BY p.publishedAt DESC")
    List<BlogPost> findPublishedByCategorySlug(@Param("slug") String slug);

    @Query("SELECT p FROM BlogPost p JOIN p.tags t WHERE t.id = :tagId")
    List<BlogPost> findByTagId(@Param("tagId") Long tagId);

    @Query("SELECT p FROM BlogPost p JOIN p.tags t WHERE t.slug = :slug AND p.published = true ORDER BY p.publishedAt DESC")
    List<BlogPost> findPublishedByTagSlug(@Param("slug") String slug);

    @Query("SELECT p FROM BlogPost p WHERE p.published = true AND (LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.content) LIKE LOWER(CONCAT('%', :query, '%'))) ORDER BY p.publishedAt DESC")
    List<BlogPost> searchPublishedByTitleOrContent(@Param("query") String query);

    @Modifying
    @Query("UPDATE BlogPost p SET p.viewCount = p.viewCount + 1 WHERE p.id = :id")
    void incrementViewCount(@Param("id") Long id);

    long countByPublishedTrue();
}
