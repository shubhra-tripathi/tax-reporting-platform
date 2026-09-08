package com.demo.taxreporting.validation;

import java.util.List;

public record ValidationResult(
        boolean valid,
        List<String> errors
) {

    public static ValidationResult success(){
        return new ValidationResult(true, List.of());
    }

    public static ValidationResult failure(String... errors) {
        return new ValidationResult(false, List.of(errors));
    }
}
