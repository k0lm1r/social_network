package com.kolmir.subscription_service.validation;

import com.kolmir.subscription_service.model.Action;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class ActionValidator implements ConstraintValidator<ValidAction, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null)
            return true;
        else {
            try {
                Action.valueOf(value);
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
    }
    
}
