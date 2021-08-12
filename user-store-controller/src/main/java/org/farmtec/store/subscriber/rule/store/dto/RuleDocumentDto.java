package org.farmtec.store.subscriber.rule.store.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.farmtec.store.subscriber.rule.store.model.RuleState;

import java.util.Date;

/**
 * Created by dp on 12/08/2021
 */

@Data
@NoArgsConstructor
public class RuleDocumentDto {
    private String id;
    private String ruleId;
    private String user;
    private String ruleName;
    private Date createdAt;
    private Date expireAt;
    private String state;
}
