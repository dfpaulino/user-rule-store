package org.farmtec.store.subscriber.rule.store.validators;

import org.farmtec.store.subscriber.rule.store.model.RuleDocument;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Date;

import static org.farmtec.store.subscriber.rule.store.validators.RuleDocumentValidatorMode.*;

/**
 * Created by dp on 04/08/2021
 */
public class ValidateRuleDocument implements ConstraintValidator<CheckRuleDocumentValid, RuleDocument> {
    private RuleDocumentValidatorMode mode;

    @Override
    public void initialize(CheckRuleDocumentValid constraintAnnotation) {
        this.mode = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(RuleDocument ruleDocument, ConstraintValidatorContext constraintValidatorContext) {
        boolean isValid = false;
        if (mode == QUERY) {
            isValid = (null != ruleDocument.getId()) ||
                    (null != ruleDocument.getUser() &&
                            (null != ruleDocument.getRuleId() || null != ruleDocument.getRuleName()));
        } else if (mode == INSERT) {
            isValid = (null != ruleDocument.getRuleName() && null != ruleDocument.getRuleId() && null != ruleDocument.getUser() &&
                    null != ruleDocument.getCreatedAt());
        } else if (mode == UPDATE) {
            isValid = (null != ruleDocument.getExpireAt()) &&
                    ((null != ruleDocument.getId()) ||
                            (null != ruleDocument.getUser() &&
                                    (null != ruleDocument.getRuleId() || null != ruleDocument.getRuleName())));
        }
        return isValid;
    }
}
