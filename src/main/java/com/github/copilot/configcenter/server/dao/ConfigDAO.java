package com.github.copilot.configcenter.server.dao;


import com.github.copilot.configcenter.entity.ConfigDO;

import java.util.List;

public interface ConfigDAO {
    /**
     * 新增配置
     *
     * @param configDO 配置对象
     * @return 配置id
     */
    long insertConfigDO(ConfigDO configDO);

    /**
     * 更新配置
     *
     * @param configDO 配置对象
     */
    void updateConfig(ConfigDO configDO);

    /**
     * 删除配置
     *
     * @param id 配置id
     */
    void delConfig(long id);

    /**
     * 获取配置
     *
     * @param id 配置id
     * @return 配置对象
     */
    ConfigDO getConfig(long id);


    /**
     * 获取全部配置
     *
     * @return 配置对象
     */
    List<ConfigDO> getAllConfig();
}
