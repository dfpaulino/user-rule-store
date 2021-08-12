package org.farmtec.store.subscriber.rule.store.mappers;

import org.farmtec.store.subscriber.rule.store.dto.RuleDocumentDto;
import org.farmtec.store.subscriber.rule.store.model.RuleDocument;
import org.mapstruct.Mapper;

/**
 * Created by dp on 12/08/2021
 */
@Mapper
public interface RuleDocumentMapper {
    RuleDocument toRuleDocument(RuleDocumentDto dto);
    RuleDocumentDto toDto(RuleDocument ruleDocument);
}
