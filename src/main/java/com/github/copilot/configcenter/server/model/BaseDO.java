package com.github.copilot.configcenter.server.model;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class BaseDO {
    /**
     * 主键
     */
    private long id;

    /**
     * 是否删除 0：否、1：是
     */
    private boolean deleted;

    /**
     * 创建人
     */
    private long createUid;

    /**
     * 更新人
     */
    private long updateUid;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
