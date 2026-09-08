package com.demo.taxreporting.ingestion;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FeedTypeResolverTest {

    private final FeedTypeResolver resolver = new FeedTypeResolver();

    @Test
    void shouldIdentifyPostingFeed() {

        assertEquals(FeedType.POSTING, resolver.resolve("raw/posting/posting.csv"));
    }

    @Test
    void shouldIdentifyAccountFeed() {
        assertEquals(FeedType.ACCOUNT_BALANCE, resolver.resolve("raw/account/account_balance.csv"));
    }

    @Test
    void shouldIdentifyTaxProfileFeed() {
        assertEquals(FeedType.CLIENT_TAX_PROFILE, resolver.resolve("raw/tax-profile/client_tax_profile.csv"));
    }

    @Test
    void shouldReturnUnknownForUnexpectedKey() {
        assertEquals(FeedType.UNKNOWN, resolver.resolve("other/file.csv"));
    }
}
