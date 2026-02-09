package com.caseyquinn.personal_website.dao.impl;

import com.caseyquinn.personal_website.dao.BlogPostDao;
import com.caseyquinn.personal_website.entity.BlogPost;
import com.caseyquinn.personal_website.exception.NotFoundException;
import com.caseyquinn.personal_website.exception.data.RetryableDataAccess;
import com.caseyquinn.personal_website.repository.BlogPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of BlogPostDao with automatic retry on transient data access failures.
 */
@Component
@RequiredArgsConstructor
@Slf4j
@RetryableDataAccess
public class BlogPostDaoImpl implements BlogPostDao {

    private final BlogPostRepository blogPostRepository;

    @Override
    public List<BlogPost> findAll() {
        log.info("DAO: Fetching all blog posts");
        return blogPostRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    public List<BlogPost> findPublished() {
        log.info("DAO: Fetching published blog posts");
        return blogPostRepository.findByPublishedTrueOrderByPublishedAtDesc();
    }

    @Override
    public Page<BlogPost> findPublished(Pageable pageable) {
        log.info("DAO: Fetching published blog posts with pagination");
        return blogPostRepository.findByPublishedTrue(pageable);
    }

    @Override
    public Optional<BlogPost> findById(Long id) {
        log.info("DAO: Fetching blog post with id: {}", id);
        return blogPostRepository.findById(id);
    }

    @Override
    public BlogPost findByIdOrThrow(Long id) {
        return findById(id)
                .orElseThrow(() -> new NotFoundException("BlogPost", id));
    }

    @Override
    public Optional<BlogPost> findByTitle(String title) {
        log.info("DAO: Fetching blog post with title: {}", title);
        return blogPostRepository.findByTitle(title);
    }

    @Override
    public Optional<BlogPost> findBySlug(String slug) {
        log.info("DAO: Fetching blog post with slug: {}", slug);
        return blogPostRepository.findBySlug(slug);
    }

    @Override
    public boolean existsByTitle(String title) {
        log.info("DAO: Checking if blog post exists with title: {}", title);
        return blogPostRepository.existsByTitle(title);
    }

    @Override
    public boolean existsBySlug(String slug) {
        log.info("DAO: Checking if blog post exists with slug: {}", slug);
        return blogPostRepository.existsBySlug(slug);
    }

    @Override
    public BlogPost save(BlogPost post) {
        log.info("DAO: Saving blog post: {}", post.getTitle());
        BlogPost saved = blogPostRepository.save(post);
        log.info("DAO: Successfully saved blog post with id: {}", saved.getId());
        return saved;
    }

    @Override
    public void deleteById(Long id) {
        log.info("DAO: Deleting blog post with id: {}", id);
        if (!blogPostRepository.existsById(id)) {
            throw new NotFoundException("BlogPost", id);
        }
        blogPostRepository.deleteById(id);
        log.info("DAO: Successfully deleted blog post with id: {}", id);
    }

    @Override
    public List<BlogPost> findByCategoryId(Long categoryId) {
        log.info("DAO: Fetching blog posts by category id: {}", categoryId);
        return blogPostRepository.findByCategoryId(categoryId);
    }

    @Override
    public List<BlogPost> findPublishedByCategorySlug(String slug) {
        log.info("DAO: Fetching published blog posts by category slug: {}", slug);
        return blogPostRepository.findPublishedByCategorySlug(slug);
    }

    @Override
    public List<BlogPost> findByTagId(Long tagId) {
        log.info("DAO: Fetching blog posts by tag id: {}", tagId);
        return blogPostRepository.findByTagId(tagId);
    }

    @Override
    public List<BlogPost> findPublishedByTagSlug(String slug) {
        log.info("DAO: Fetching published blog posts by tag slug: {}", slug);
        return blogPostRepository.findPublishedByTagSlug(slug);
    }

    @Override
    public List<BlogPost> searchPublished(String query) {
        log.info("DAO: Searching published blog posts with query: {}", query);
        return blogPostRepository.searchPublishedByTitleOrContent(query);
    }

    @Override
    public void incrementViewCount(Long id) {
        log.info("DAO: Incrementing view count for blog post with id: {}", id);
        blogPostRepository.incrementViewCount(id);
    }

    @Override
    public long count() {
        log.info("DAO: Counting total blog posts");
        return blogPostRepository.count();
    }

    @Override
    public long countPublished() {
        log.info("DAO: Counting published blog posts");
        return blogPostRepository.countByPublishedTrue();
    }
}
