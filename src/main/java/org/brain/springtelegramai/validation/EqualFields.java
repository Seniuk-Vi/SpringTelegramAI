package org.brain.springtelegramai.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.brain.springtelegramai.validation.validators.EqualFieldsValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {EqualFieldsValidator.class})
public @interface EqualFields {
    String message() default "Fields not equal";

    String[] value();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
