package com.github.copilot.configcenter.client.model;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.time.LocalDateTime;
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
