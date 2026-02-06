package com.caseyquinn.personal_website.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CloudinaryUploadResult {
    private String url;
    private String secureUrl;
    private String publicId;
    private String format;
    private Long bytes;
    private Integer width;
    private Integer height;
}
