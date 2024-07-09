package com.github.copilot.configcenter.client.model;

import lombok.Data;

import java.util.List;


@Data
public class ConfigBO {

    /**
     * 配置id
     */
    private long id;

    /**
     * 配置版本号
     */
    private int version;

    private List<ConfigDataBO> configDataList;

}
