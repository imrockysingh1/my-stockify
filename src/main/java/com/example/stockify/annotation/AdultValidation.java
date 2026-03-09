package com.example.stockify.annotation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Constraint(validatedBy = {AdultValidator.class})
public @interface AdultValidation {
    String message() default "Role is required";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};}
