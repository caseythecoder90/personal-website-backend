package com.caseyquinn.personal_website.dao.impl;

import com.caseyquinn.personal_website.dao.BlogTagDao;
import com.caseyquinn.personal_website.entity.BlogTag;
import com.caseyquinn.personal_website.exception.NotFoundException;
import com.caseyquinn.personal_website.exception.data.RetryableDataAccess;
import com.caseyquinn.personal_website.repository.BlogTagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of BlogTagDao with automatic retry on transient data access failures.
 */
@Component
@RequiredArgsConstructor
@Slf4j
@RetryableDataAccess
public class BlogTagDaoImpl implements BlogTagDao {

    private final BlogTagRepository blogTagRepository;

    @Override
    public List<BlogTag> findAll() {
        log.info("DAO: Fetching all blog tags");
        return blogTagRepository.findAllByOrderByNameAsc();
    }

    @Override
    public List<BlogTag> findAllByUsage() {
        log.info("DAO: Fetching all blog tags by usage count");
        return blogTagRepository.findAllByOrderByUsageCountDesc();
    }

    @Override
    public List<BlogTag> findPopular() {
        log.info("DAO: Fetching top 10 popular blog tags");
        return blogTagRepository.findTop10ByOrderByUsageCountDesc();
    }

    @Override
    public Optional<BlogTag> findById(Long id) {
        log.info("DAO: Fetching blog tag with id: {}", id);
        return blogTagRepository.findById(id);
    }

    @Override
    public BlogTag findByIdOrThrow(Long id) {
        return findById(id)
                .orElseThrow(() -> new NotFoundException("BlogTag", id));
    }

    @Override
    public Optional<BlogTag> findByName(String name) {
        log.info("DAO: Fetching blog tag with name: {}", name);
        return blogTagRepository.findByName(name);
    }

    @Override
    public Optional<BlogTag> findBySlug(String slug) {
        log.info("DAO: Fetching blog tag with slug: {}", slug);
        return blogTagRepository.findBySlug(slug);
    }

    @Override
    public boolean existsByName(String name) {
        log.info("DAO: Checking if blog tag exists with name: {}", name);
        return blogTagRepository.existsByName(name);
    }

    @Override
    public boolean existsBySlug(String slug) {
        log.info("DAO: Checking if blog tag exists with slug: {}", slug);
        return blogTagRepository.existsBySlug(slug);
    }

    @Override
    public BlogTag save(BlogTag tag) {
        log.info("DAO: Saving blog tag: {}", tag.getName());
        BlogTag saved = blogTagRepository.save(tag);
        log.info("DAO: Successfully saved blog tag with id: {}", saved.getId());
        return saved;
    }

    @Override
    public void deleteById(Long id) {
        log.info("DAO: Deleting blog tag with id: {}", id);
        if (!blogTagRepository.existsById(id)) {
            throw new NotFoundException("BlogTag", id);
        }
        blogTagRepository.deleteById(id);
        log.info("DAO: Successfully deleted blog tag with id: {}", id);
    }

    @Override
    public void incrementUsageCount(Long id) {
        log.info("DAO: Incrementing usage count for blog tag with id: {}", id);
        blogTagRepository.incrementUsageCount(id);
    }

    @Override
    public void decrementUsageCount(Long id) {
        log.info("DAO: Decrementing usage count for blog tag with id: {}", id);
        blogTagRepository.decrementUsageCount(id);
    }

    @Override
    public long count() {
        log.info("DAO: Counting total blog tags");
        return blogTagRepository.count();
    }
}
