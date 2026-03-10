package com.example.stockify.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.Period;

public class AdultValidator implements ConstraintValidator<AdultValidation, LocalDate> {

    @Override
    public boolean isValid(LocalDate dob, ConstraintValidatorContext constraintValidatorContext) {
        if(dob == null) return false;
        int age = Period.between(dob , LocalDate.now()).getYears();
        if(age >= 18 ) return true;

        else return false;
    }
}
