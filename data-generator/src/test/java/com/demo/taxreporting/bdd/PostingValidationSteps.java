package com.demo.taxreporting.bdd;

import com.demo.taxreporting.model.IncomeType;
import com.demo.taxreporting.model.Posting;
import com.demo.taxreporting.validation.PostingValidator;
import com.demo.taxreporting.validation.ValidationResult;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class PostingValidationSteps {

    private Posting posting;
    private ValidationResult validationResult;

    private final PostingValidator validator = new PostingValidator();

    @Given("a posting with id {string}, account {string} and amount {int}")
    public void aPostingWithIdAccountAndAmount(
            String postingId,
            String accountId,
            int amount) {
        posting = new Posting(
                postingId,
                accountId,
                OffsetDateTime.parse(
                        "2026-08-25T10:30:00+01:00"
                ),
                IncomeType.INTEREST,
                BigDecimal.valueOf(amount),
                "GBP"
        );
    }

    @When("the posting is validated")
    public void thePostingIsValidated() {
        validationResult = validator.validate(posting);
    }

    @Then("the posting should be valid")
    public void thePostingShouldBeValid() {
        assertTrue(validationResult.valid());
    }

    @Then("the posting should be invalid")
    public void thePostingShouldBeInvalid() {
        assertFalse(validationResult.valid());
    }

}
