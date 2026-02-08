package com.caseyquinn.personal_website.controller;

import com.caseyquinn.personal_website.annotations.ProjectApiResponses;
import com.caseyquinn.personal_website.dto.request.CreateProjectRequest;
import com.caseyquinn.personal_website.dto.request.UpdateProjectRequest;
import com.caseyquinn.personal_website.dto.response.ProjectResponse;
import com.caseyquinn.personal_website.dto.response.Response;
import com.caseyquinn.personal_website.service.ProjectService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing portfolio projects.
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Projects", description = "Project management APIs")
public class ProjectController {

    private final ProjectService projectService;

    /**
     * Retrieves all projects without pagination.
     *
     * @return response entity containing list of all projects
     */
    @ProjectApiResponses.GetAll
    @GetMapping("/projects")
    public ResponseEntity<Response<List<ProjectResponse>>> getAllProjects() {
        log.info("Fetching all projects");
        List<ProjectResponse> projects = projectService.getAllProjects();
        return ResponseEntity.ok(Response.success(projects, "Projects retrieved successfully"));
    }
    
    /**
     * Retrieves projects with pagination support.
     *
     * @param pageable pagination parameters (default size: 10)
     * @return response entity containing paginated projects
     */
    @ProjectApiResponses.GetPaginated
    @GetMapping("/projects/paginated")
    public ResponseEntity<Response<Page<ProjectResponse>>> getProjectsPaginated(
            @PageableDefault(size = 10) Pageable pageable) {
        log.info("Fetching projects with pagination: {}", pageable);
        Page<ProjectResponse> projects = projectService.getProjectsPaginated(pageable);
        return ResponseEntity.ok(Response.success(projects, "Projects retrieved successfully"));
    }
    
    /**
     * Retrieves a specific project by its ID.
     *
     * @param id the project ID
     * @return response entity containing the project
     */
    @ProjectApiResponses.GetById
    @GetMapping("/projects/{id}")
    public ResponseEntity<Response<ProjectResponse>> getProject(
            @Parameter(description = "Project ID") @PathVariable Long id) {
        log.info("Fetching project with id: {}", id);
        ProjectResponse project = projectService.getProjectById(id);
        return ResponseEntity.ok(Response.success(project, "Project retrieved successfully"));
    }
    
    /**
     * Creates a new project.
     *
     * @param request the project creation request
     * @return response entity containing the created project with HTTP 201 status
     */
    @ProjectApiResponses.Create
    @PostMapping("/projects")
    public ResponseEntity<Response<ProjectResponse>> createProject(
            @Valid @RequestBody CreateProjectRequest request) {
        log.info("Creating new project: {}", request.getName());
        ProjectResponse project = projectService.createProject(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.success(project, "Project created successfully"));
    }
    
    /**
     * Updates an existing project.
     *
     * @param id the project ID
     * @param request the project update request
     * @return response entity containing the updated project
     */
    @ProjectApiResponses.Update
    @PutMapping("/projects/{id}")
    public ResponseEntity<Response<ProjectResponse>> updateProject(
            @Parameter(description = "Project ID") @PathVariable Long id,
            @Valid @RequestBody UpdateProjectRequest request) {
        log.info("Updating project with id: {}", id);
        ProjectResponse project = projectService.updateProject(id, request);
        return ResponseEntity.ok(Response.success(project, "Project updated successfully"));
    }
    
    /**
     * Deletes a project by its ID.
     *
     * @param id the project ID
     * @return response entity with success message
     */
    @ProjectApiResponses.Delete
    @DeleteMapping("/projects/{id}")
    public ResponseEntity<Response<Void>> deleteProject(
            @Parameter(description = "Project ID") @PathVariable Long id) {
        log.info("Deleting project with id: {}", id);
        projectService.deleteProject(id);
        return ResponseEntity.ok(Response.success(null, "Project deleted successfully"));
    }
    
    /**
     * Retrieves projects that use a specific technology.
     *
     * @param technology the technology name to filter by
     * @return response entity containing list of matching projects
     */
    @ProjectApiResponses.GetByTechnology
    @GetMapping("/projects/technology/{technology}")
    public ResponseEntity<Response<List<ProjectResponse>>> getProjectsByTechnology(
            @Parameter(description = "Technology name") @PathVariable String technology) {
        log.info("Fetching projects by technology: {}", technology);
        List<ProjectResponse> projects = projectService.getProjectsByTechnologyName(technology);
        return ResponseEntity.ok(Response.success(projects, "Projects retrieved successfully"));
    }
}