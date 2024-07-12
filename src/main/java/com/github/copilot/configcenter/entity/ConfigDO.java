package com.github.copilot.configcenter.entity;

import com.github.copilot.db.BaseEntity;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;

@Entity
@Table(name = "config")
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class ConfigDO extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "config_seq")
    @SequenceGenerator(name = "config_seq",
            sequenceName = "config_seq",
            allocationSize = 1)
    private Long id;

    private String name;

    @Version
    private Integer version;

    private String configData;


}
