package com.caseyquinn.personal_website.repository;

import com.caseyquinn.personal_website.entity.ProjectImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectImageRepository extends JpaRepository<ProjectImage, Long> {

    List<ProjectImage> findByProjectIdOrderByDisplayOrderAsc(Long projectId);

    Optional<ProjectImage> findByProjectIdAndIsPrimaryTrue(Long projectId);

    long countByProjectId(Long projectId);

    void deleteByProjectId(Long projectId);
}
