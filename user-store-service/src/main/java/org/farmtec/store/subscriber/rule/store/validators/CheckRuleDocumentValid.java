package org.farmtec.store.subscriber.rule.store.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.farmtec.store.subscriber.rule.store.validators.RuleDocumentValidatorMode.*;

/**
 * Created by dp on 04/08/2021
 */
@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = ValidateRuleDocument.class)
public @interface CheckRuleDocumentValid {
    String message() default "{RuleDocument not valid for mode}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    RuleDocumentValidatorMode value() default QUERY;
}
