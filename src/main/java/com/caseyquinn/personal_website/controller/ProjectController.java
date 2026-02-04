package com.caseyquinn.personal_website.controller;

import com.caseyquinn.personal_website.dto.request.CreateProjectRequest;
import com.caseyquinn.personal_website.dto.request.UpdateProjectRequest;
import com.caseyquinn.personal_website.dto.response.Response;
import com.caseyquinn.personal_website.dto.response.HealthResponse;
import com.caseyquinn.personal_website.dto.response.ProjectResponse;
import com.caseyquinn.personal_website.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Projects", description = "Project management APIs")
public class ProjectController {
    
    private final ProjectService projectService;
    
    @Value("${spring.application.name}")
    private String applicationName;
    
    @Value("${app.version:1.0.0}")
    private String version;
    
    @Value("${spring.profiles.active:unknown}")
    private String environment;
    
    @Operation(summary = "Health check", description = "Get service health status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Service is healthy")
    })
    @GetMapping("/health")
    public ResponseEntity<Response<HealthResponse>> health() {
        log.debug("Health check requested");
        
        HealthResponse health = HealthResponse.builder()
                .status("UP")
                .service(applicationName)
                .version(version)
                .environment(environment)
                .timestamp(LocalDateTime.now())
                .build();
                
        return ResponseEntity.ok(Response.success(health, "Service is healthy"));
    }
    
    @Operation(summary = "Get all projects", description = "Retrieve a list of all projects")
    @GetMapping("/projects")
    public ResponseEntity<Response<List<ProjectResponse>>> getAllProjects() {
        log.info("Fetching all projects");
        List<ProjectResponse> projects = projectService.getAllProjects();
        return ResponseEntity.ok(Response.success(projects, "Projects retrieved successfully"));
    }
    
    @Operation(summary = "Get projects with pagination", description = "Retrieve projects with pagination support")
    @GetMapping("/projects/paginated")
    public ResponseEntity<Response<Page<ProjectResponse>>> getProjectsPaginated(
            @PageableDefault(size = 10) Pageable pageable) {
        log.info("Fetching projects with pagination: {}", pageable);
        Page<ProjectResponse> projects = projectService.getProjectsPaginated(pageable);
        return ResponseEntity.ok(Response.success(projects, "Projects retrieved successfully"));
    }
    
    @Operation(summary = "Get project by ID", description = "Retrieve a specific project by its ID")
    @GetMapping("/projects/{id}")
    public ResponseEntity<Response<ProjectResponse>> getProject(
            @Parameter(description = "Project ID") @PathVariable Long id) {
        log.info("Fetching project with id: {}", id);
        ProjectResponse project = projectService.getProjectById(id);
        return ResponseEntity.ok(Response.success(project, "Project retrieved successfully"));
    }
    
    @Operation(summary = "Create new project", description = "Create a new project")
    @PostMapping("/projects")
    public ResponseEntity<Response<ProjectResponse>> createProject(
            @Valid @RequestBody CreateProjectRequest request) {
        log.info("Creating new project: {}", request.getName());
        ProjectResponse project = projectService.createProject(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.success(project, "Project created successfully"));
    }
    
    @Operation(summary = "Update project", description = "Update an existing project")
    @PutMapping("/projects/{id}")
    public ResponseEntity<Response<ProjectResponse>> updateProject(
            @Parameter(description = "Project ID") @PathVariable Long id,
            @Valid @RequestBody UpdateProjectRequest request) {
        log.info("Updating project with id: {}", id);
        ProjectResponse project = projectService.updateProject(id, request);
        return ResponseEntity.ok(Response.success(project, "Project updated successfully"));
    }
    
    @Operation(summary = "Delete project", description = "Delete a project by ID")
    @DeleteMapping("/projects/{id}")
    public ResponseEntity<Response<Void>> deleteProject(
            @Parameter(description = "Project ID") @PathVariable Long id) {
        log.info("Deleting project with id: {}", id);
        projectService.deleteProject(id);
        return ResponseEntity.ok(Response.success(null, "Project deleted successfully"));
    }
    
    @Operation(summary = "Get projects by technology", description = "Retrieve projects that use a specific technology")
    @GetMapping("/projects/technology/{technology}")
    public ResponseEntity<Response<List<ProjectResponse>>> getProjectsByTechnology(
            @Parameter(description = "Technology name") @PathVariable String technology) {
        log.info("Fetching projects by technology: {}", technology);
        List<ProjectResponse> projects = projectService.getProjectsByTechnology(technology);
        return ResponseEntity.ok(Response.success(projects, "Projects retrieved successfully"));
    }
}