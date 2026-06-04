package com.petshop.web.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class SomenteLetrasValidator implements ConstraintValidator<SomenteLetras, String> {

    private static final Pattern LETRAS_E_ESPACOS = Pattern.compile("^[\\p{L} ]+$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }
        return LETRAS_E_ESPACOS.matcher(value.trim()).matches();
    }
}
