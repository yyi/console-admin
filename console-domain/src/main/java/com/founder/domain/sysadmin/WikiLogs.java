package com.founder.domain.sysadmin;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @creaor:yyi
 * @createDate:2019/5/17
 * @Describle
 */
@Entity
@Table(name = "wikilogs", schema = "clickstream")
@Data
@EqualsAndHashCode
public class WikiLogs {
    private String previous_id;
    @Id
    private String currentId;
    private Integer noOccurences;
    private String previousTitle;
    private String currentTitle;
    private String type;
}
