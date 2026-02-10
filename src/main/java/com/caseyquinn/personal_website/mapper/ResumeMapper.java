package com.caseyquinn.personal_website.mapper;

import com.caseyquinn.personal_website.dto.response.ResumeResponse;
import com.caseyquinn.personal_website.entity.Resume;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * MapStruct mapper for converting between Resume entities and DTOs.
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ResumeMapper {

    /**
     * Converts a Resume entity to a response DTO.
     *
     * @param resume the entity
     * @return the response DTO
     */
    ResumeResponse toResponse(Resume resume);
}
