package org.brain.springtelegramai.validation.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.brain.springtelegramai.validation.EqualFields;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EqualFieldsValidator implements ConstraintValidator<EqualFields, Object> {
    private static final ExpressionParser PARSER = new SpelExpressionParser();
    private String[] fields;

    @Override
    public void initialize(EqualFields constraintAnnotation) {
        fields = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        Set<Object> set = Stream.of(fields)
                .map(field -> PARSER.parseExpression(field).getValue(value))
                .collect(Collectors.toSet());
        return set.size() < fields.length;
    }
}
