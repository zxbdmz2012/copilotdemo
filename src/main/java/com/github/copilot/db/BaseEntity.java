package com.github.copilot.db;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import java.util.Date;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class BaseEntity {

    @CreatedBy
    @Column(name = "create_by", length = 50)
    private String createBy;

    @LastModifiedBy
    @Column(name = "modified_by", length = 50)
    private String updateBy;

    @CreatedDate
    @Column(name = "create_date")
    private Date createTime;

    @LastModifiedDate
    @Column(name = "modified_date")
    private Date updateTime;


}
