package com.demo.taxreporting.mapper;

import com.demo.taxreporting.model.ClientTaxProfile;
import com.demo.taxreporting.model.CrsStatus;
import com.demo.taxreporting.model.FatcaStatus;

public class ClientTaxProfileMapper {

    public ClientTaxProfile map(long customerId) {

        String clientId = "C" + customerId;

        int selector = (int) (customerId % 3);

        return switch (selector) {
            case 0 -> new ClientTaxProfile(
                    clientId,
                    "US",
                    FatcaStatus.US_PERSON,
                    CrsStatus.REPORTABLE
            );

            case 1 -> new ClientTaxProfile(
                    clientId,
                    "GB",
                    FatcaStatus.NON_US,
                    CrsStatus.REPORTABLE
            );

            default -> new ClientTaxProfile(
                    clientId,
                    "DE",
                    FatcaStatus.NON_US,
                    CrsStatus.REPORTABLE
            );
        };
    }
}
