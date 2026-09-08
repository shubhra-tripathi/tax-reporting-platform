package com.demo.taxreporting.ingestion;

public class FeedTypeResolver {

    public FeedType resolve(String objectKey) {

        if(objectKey == null) {
            return FeedType.UNKNOWN;
        }

        if(objectKey.startsWith("raw/posting")) {
            return FeedType.POSTING;
        }

        if(objectKey.startsWith("raw/account")) {
            return FeedType.ACCOUNT_BALANCE;
        }

        if(objectKey.startsWith("raw/tax-profile")) {
            return FeedType.CLIENT_TAX_PROFILE;
        }

        return FeedType.UNKNOWN;
    }
}
