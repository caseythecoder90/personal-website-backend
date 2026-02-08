package com.caseyquinn.personal_website.entity.enums;

import lombok.Getter;

@Getter
public enum CertificationStatus {
    EARNED("Earned"),
    IN_PROGRESS("In Progress"),
    EXPIRED("Expired");

    private final String displayName;

    CertificationStatus(String displayName) {
        this.displayName = displayName;
    }

}
