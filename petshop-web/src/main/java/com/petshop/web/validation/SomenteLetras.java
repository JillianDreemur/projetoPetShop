package com.petshop.web.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = SomenteLetrasValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface SomenteLetras {

    String message() default "Use apenas letras e espaços (sem números ou símbolos)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
