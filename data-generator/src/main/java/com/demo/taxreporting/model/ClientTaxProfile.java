package com.demo.taxreporting.model;

public record ClientTaxProfile(
        String clientId,
        String taxResidence,
        FatcaStatus fatcaStatus,
        CrsStatus crsStatus

) {
}
