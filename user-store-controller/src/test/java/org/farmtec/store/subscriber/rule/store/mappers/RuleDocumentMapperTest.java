package org.farmtec.store.subscriber.rule.store.mappers;

import org.assertj.core.api.AssertionsForClassTypes;
import org.farmtec.store.subscriber.rule.store.dto.RuleDocumentDto;
import org.farmtec.store.subscriber.rule.store.model.RuleDocument;
import org.farmtec.store.subscriber.rule.store.model.RuleState;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @Author Daniel Paulino 12/08/2021
 */
class RuleDocumentMapperTest {

    private RuleDocumentMapper mapper = new RuleDocumentMapperImpl();
    private static RuleDocumentDto dto;
    private static RuleDocument document;

    @BeforeAll
    public static void setUp() {
        dto = new RuleDocumentDto();
        dto.setRuleId("1");
        dto.setCreatedAt(new Date());
        dto.setUser("dan");
        dto.setRuleName("rule_1");
        dto.setState("CREATED");

        document = new RuleDocument();
        document.setUser("dan");
        document.setRuleName("rule_1");
        document.setRuleId("1");
        document.setExpireAt(new Date());
        document.setState(RuleState.ACK);
    }

    @Test
    void toRuleDocument() {

        RuleDocument ruleDocument = mapper.toRuleDocument(dto);

        assertAll(
                () -> assertThat(ruleDocument.getState()).isEqualTo(RuleState.CREATED),
                () -> assertThat(ruleDocument.getRuleId()).isEqualTo("1"),
                () -> assertThat(ruleDocument.getUser()).isEqualTo("dan"),
                () -> assertThat(ruleDocument.getRuleName()).isEqualTo("rule_1")

                );
    }

    @Test
    void toRuleDocument_whenStateNotSentShouldBeCREATED() {

        RuleDocument ruleDocument = mapper.toRuleDocument(dto);
        assertAll(
                () -> assertThat(ruleDocument.getState()).isEqualTo(RuleState.CREATED)
        );
    }

    @Test
    void toDto() {
        RuleDocumentDto dto = mapper.toDto(document);
        assertAll(
                () -> assertThat(dto.getState()).isEqualTo(RuleState.ACK.name()),
                () -> AssertionsForClassTypes.assertThat(dto.getRuleId()).isEqualTo("1"),
                () -> AssertionsForClassTypes.assertThat(dto.getUser()).isEqualTo("dan"),
                () -> AssertionsForClassTypes.assertThat(dto.getRuleName()).isEqualTo("rule_1")
        );
    }
}