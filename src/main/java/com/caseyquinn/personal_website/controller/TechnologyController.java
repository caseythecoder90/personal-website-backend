package com.caseyquinn.personal_website.controller;

import com.caseyquinn.personal_website.annotations.TechnologyApiResponses;
import com.caseyquinn.personal_website.dto.request.CreateTechnologyRequest;
import com.caseyquinn.personal_website.dto.request.UpdateTechnologyRequest;
import com.caseyquinn.personal_website.dto.response.Response;
import com.caseyquinn.personal_website.dto.response.TechnologyResponse;
import com.caseyquinn.personal_website.entity.enums.ProficiencyLevel;
import com.caseyquinn.personal_website.entity.enums.TechnologyCategory;
import com.caseyquinn.personal_website.service.TechnologyService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for managing technologies and skills.
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Technologies", description = "Technology and skills management APIs")
public class TechnologyController {

    private final TechnologyService technologyService;

    /**
     * Retrieves all technologies.
     *
     * @return response entity containing list of all technologies
     */
    @TechnologyApiResponses.GetAll
    @GetMapping("/technologies")
    public ResponseEntity<Response<List<TechnologyResponse>>> getAllTechnologies() {
        log.info("Fetching all technologies");
        List<TechnologyResponse> technologies = technologyService.getAllTechnologies();
        return ResponseEntity.ok(Response.success(technologies, "Technologies retrieved successfully"));
    }

    /**
     * Retrieves technologies with pagination.
     *
     * @param pageable pagination parameters
     * @return response entity containing paginated technologies
     */
    @TechnologyApiResponses.GetPaginated
    @GetMapping("/technologies/paginated")
    public ResponseEntity<Response<Page<TechnologyResponse>>> getTechnologiesPaginated(Pageable pageable) {
        log.info("Fetching technologies with pagination: {}", pageable);
        Page<TechnologyResponse> technologies = technologyService.getTechnologiesPaginated(pageable);
        return ResponseEntity.ok(Response.success(technologies, "Technologies retrieved successfully"));
    }

    /**
     * Retrieves a specific technology by its ID.
     *
     * @param id the technology ID
     * @return response entity containing the technology
     */
    @TechnologyApiResponses.GetById
    @GetMapping("/technologies/{id}")
    public ResponseEntity<Response<TechnologyResponse>> getTechnology(
            @Parameter(description = "Technology ID") @PathVariable Long id) {
        log.info("Fetching technology with id: {}", id);
        TechnologyResponse technology = technologyService.getTechnologyById(id);
        return ResponseEntity.ok(Response.success(technology, "Technology retrieved successfully"));
    }

    /**
     * Retrieves a technology by its name.
     *
     * @param name the technology name
     * @return response entity containing the technology
     */
    @TechnologyApiResponses.GetByName
    @GetMapping("/technologies/name/{name}")
    public ResponseEntity<Response<TechnologyResponse>> getTechnologyByName(
            @Parameter(description = "Technology name") @PathVariable String name) {
        log.info("Fetching technology with name: {}", name);
        TechnologyResponse technology = technologyService.getTechnologyByName(name);
        return ResponseEntity.ok(Response.success(technology, "Technology retrieved successfully"));
    }

    /**
     * Retrieves technologies filtered by category.
     *
     * @param category the technology category
     * @return response entity containing list of matching technologies
     */
    @TechnologyApiResponses.GetByCategory
    @GetMapping("/technologies/category/{category}")
    public ResponseEntity<Response<List<TechnologyResponse>>> getTechnologiesByCategory(
            @Parameter(description = "Technology category") @PathVariable TechnologyCategory category) {
        log.info("Fetching technologies by category: {}", category);
        List<TechnologyResponse> technologies = technologyService.getTechnologiesByCategory(category);
        return ResponseEntity.ok(Response.success(technologies, "Technologies retrieved successfully"));
    }

    /**
     * Retrieves technologies filtered by proficiency level.
     *
     * @param level the proficiency level
     * @return response entity containing list of matching technologies
     */
    @TechnologyApiResponses.GetByProficiency
    @GetMapping("/technologies/proficiency/{level}")
    public ResponseEntity<Response<List<TechnologyResponse>>> getTechnologiesByProficiency(
            @Parameter(description = "Proficiency level") @PathVariable ProficiencyLevel level) {
        log.info("Fetching technologies by proficiency level: {}", level);
        List<TechnologyResponse> technologies = technologyService.getTechnologiesByProficiencyLevel(level);
        return ResponseEntity.ok(Response.success(technologies, "Technologies retrieved successfully"));
    }

    /**
     * Retrieves featured technologies.
     *
     * @return response entity containing list of featured technologies
     */
    @TechnologyApiResponses.GetFeatured
    @GetMapping("/technologies/featured")
    public ResponseEntity<Response<List<TechnologyResponse>>> getFeaturedTechnologies() {
        log.info("Fetching featured technologies");
        List<TechnologyResponse> technologies = technologyService.getFeaturedTechnologies();
        return ResponseEntity.ok(Response.success(technologies, "Featured technologies retrieved successfully"));
    }

    /**
     * Retrieves technologies sorted by usage count across projects.
     *
     * @return response entity containing list of most used technologies
     */
    @TechnologyApiResponses.GetMostUsed
    @GetMapping("/technologies/most-used")
    public ResponseEntity<Response<List<TechnologyResponse>>> getMostUsedTechnologies() {
        log.info("Fetching most used technologies");
        List<TechnologyResponse> technologies = technologyService.getMostUsedTechnologies();
        return ResponseEntity.ok(Response.success(technologies, "Most used technologies retrieved successfully"));
    }

    /**
     * Creates a new technology.
     *
     * @param request the technology creation request
     * @return response entity containing the created technology with HTTP 201 status
     */
    @TechnologyApiResponses.Create
    @PostMapping("/technologies")
    public ResponseEntity<Response<TechnologyResponse>> createTechnology(
            @Valid @RequestBody CreateTechnologyRequest request) {
        log.info("Creating new technology: {}", request.getName());
        TechnologyResponse technology = technologyService.createTechnology(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.success(technology, "Technology created successfully"));
    }

    /**
     * Updates an existing technology.
     *
     * @param id the technology ID
     * @param request the technology update request
     * @return response entity containing the updated technology
     */
    @TechnologyApiResponses.Update
    @PutMapping("/technologies/{id}")
    public ResponseEntity<Response<TechnologyResponse>> updateTechnology(
            @Parameter(description = "Technology ID") @PathVariable Long id,
            @Valid @RequestBody UpdateTechnologyRequest request) {
        log.info("Updating technology with id: {}", id);
        TechnologyResponse technology = technologyService.updateTechnology(id, request);
        return ResponseEntity.ok(Response.success(technology, "Technology updated successfully"));
    }

    /**
     * Deletes a technology by its ID.
     *
     * @param id the technology ID
     * @return response entity with success message
     */
    @TechnologyApiResponses.Delete
    @DeleteMapping("/technologies/{id}")
    public ResponseEntity<Response<Void>> deleteTechnology(
            @Parameter(description = "Technology ID") @PathVariable Long id) {
        log.info("Deleting technology with id: {}", id);
        technologyService.deleteTechnology(id);
        return ResponseEntity.ok(Response.success(null, "Technology deleted successfully"));
    }
}
