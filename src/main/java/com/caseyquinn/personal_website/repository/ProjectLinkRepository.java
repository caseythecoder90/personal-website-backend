package com.caseyquinn.personal_website.repository;

import com.caseyquinn.personal_website.entity.ProjectLink;
import com.caseyquinn.personal_website.entity.enums.LinkType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for ProjectLink entity operations.
 */
@Repository
public interface ProjectLinkRepository extends JpaRepository<ProjectLink, Long> {

    List<ProjectLink> findByProjectIdOrderByLinkTypeAscDisplayOrderAsc(Long projectId);

    List<ProjectLink> findByProjectIdAndLinkTypeOrderByDisplayOrderAsc(Long projectId, LinkType linkType);

    long countByProjectId(Long projectId);

    void deleteByProjectId(Long projectId);
}
