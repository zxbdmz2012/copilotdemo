package com.github.copilot.configcenter.client.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Data
public class ConfigDataBO {

    /**
     * 自动刷新的bean字段列表
     */
    List<RefreshFieldBO> refreshFieldList;
    /**
     * 配置key
     */
    private String key;
    /**
     * 配置值
     */
    private String value;

    public ConfigDataBO(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public void addRefreshField(RefreshFieldBO refreshFieldBO) {
        Optional.ofNullable(refreshFieldList).orElseGet(() -> refreshFieldList = new ArrayList<>()).add(refreshFieldBO);
    }
}
