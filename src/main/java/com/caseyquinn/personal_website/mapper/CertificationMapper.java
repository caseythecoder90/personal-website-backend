package com.caseyquinn.personal_website.mapper;

import com.caseyquinn.personal_website.dto.request.CreateCertificationRequest;
import com.caseyquinn.personal_website.dto.request.UpdateCertificationRequest;
import com.caseyquinn.personal_website.dto.response.CertificationResponse;
import com.caseyquinn.personal_website.entity.Certification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * MapStruct mapper for converting between Certification entities and DTOs.
 */
@Mapper(
    componentModel = "spring",
    uses = {TechnologyMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CertificationMapper {

    @Mapping(target = "technologies", source = "technologies")
    CertificationResponse toResponse(Certification certification);

    List<CertificationResponse> toResponseList(List<Certification> certifications);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "technologies", ignore = true)
    Certification toEntity(CreateCertificationRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "technologies", ignore = true)
    void updateEntityFromRequest(UpdateCertificationRequest request, @MappingTarget Certification certification);
}
