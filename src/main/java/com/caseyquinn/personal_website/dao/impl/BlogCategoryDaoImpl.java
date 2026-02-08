package com.caseyquinn.personal_website.dao.impl;

import com.caseyquinn.personal_website.dao.BlogCategoryDao;
import com.caseyquinn.personal_website.entity.BlogCategory;
import com.caseyquinn.personal_website.exception.NotFoundException;
import com.caseyquinn.personal_website.exception.data.RetryableDataAccess;
import com.caseyquinn.personal_website.repository.BlogCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of BlogCategoryDao with automatic retry on transient data access failures.
 */
@Component
@RequiredArgsConstructor
@Slf4j
@RetryableDataAccess
public class BlogCategoryDaoImpl implements BlogCategoryDao {

    private final BlogCategoryRepository blogCategoryRepository;

    @Override
    public List<BlogCategory> findAll() {
        log.info("DAO: Fetching all blog categories");
        return blogCategoryRepository.findAllByOrderByNameAsc();
    }

    @Override
    public Optional<BlogCategory> findById(Long id) {
        log.info("DAO: Fetching blog category with id: {}", id);
        return blogCategoryRepository.findById(id);
    }

    @Override
    public BlogCategory findByIdOrThrow(Long id) {
        return findById(id)
                .orElseThrow(() -> new NotFoundException("BlogCategory", id));
    }

    @Override
    public Optional<BlogCategory> findByName(String name) {
        log.info("DAO: Fetching blog category with name: {}", name);
        return blogCategoryRepository.findByName(name);
    }

    @Override
    public Optional<BlogCategory> findBySlug(String slug) {
        log.info("DAO: Fetching blog category with slug: {}", slug);
        return blogCategoryRepository.findBySlug(slug);
    }

    @Override
    public boolean existsByName(String name) {
        log.info("DAO: Checking if blog category exists with name: {}", name);
        return blogCategoryRepository.existsByName(name);
    }

    @Override
    public boolean existsBySlug(String slug) {
        log.info("DAO: Checking if blog category exists with slug: {}", slug);
        return blogCategoryRepository.existsBySlug(slug);
    }

    @Override
    public BlogCategory save(BlogCategory category) {
        log.info("DAO: Saving blog category: {}", category.getName());
        BlogCategory saved = blogCategoryRepository.save(category);
        log.info("DAO: Successfully saved blog category with id: {}", saved.getId());
        return saved;
    }

    @Override
    public void deleteById(Long id) {
        log.info("DAO: Deleting blog category with id: {}", id);
        if (!blogCategoryRepository.existsById(id)) {
            throw new NotFoundException("BlogCategory", id);
        }
        blogCategoryRepository.deleteById(id);
        log.info("DAO: Successfully deleted blog category with id: {}", id);
    }

    @Override
    public long count() {
        log.info("DAO: Counting total blog categories");
        return blogCategoryRepository.count();
    }
}
