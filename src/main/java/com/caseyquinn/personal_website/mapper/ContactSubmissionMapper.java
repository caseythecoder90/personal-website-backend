package com.caseyquinn.personal_website.mapper;

import com.caseyquinn.personal_website.dto.request.ContactSubmissionRequest;
import com.caseyquinn.personal_website.dto.response.ContactSubmissionResponse;
import com.caseyquinn.personal_website.entity.ContactSubmission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * MapStruct mapper for converting between ContactSubmission entities and DTOs.
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ContactSubmissionMapper {

    /**
     * Converts a ContactSubmission entity to a response DTO.
     *
     * @param submission the entity
     * @return the response DTO
     */
    ContactSubmissionResponse toResponse(ContactSubmission submission);

    /**
     * Converts a list of ContactSubmission entities to response DTOs.
     *
     * @param submissions the entity list
     * @return the response DTO list
     */
    List<ContactSubmissionResponse> toResponseList(List<ContactSubmission> submissions);

    /**
     * Converts a ContactSubmissionRequest to a new entity.
     *
     * @param request the submission request
     * @return the entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "ipAddress", ignore = true)
    @Mapping(target = "userAgent", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "respondedAt", ignore = true)
    ContactSubmission toEntity(ContactSubmissionRequest request);
}
