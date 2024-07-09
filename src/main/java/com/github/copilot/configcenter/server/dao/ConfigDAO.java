package com.github.copilot.configcenter.server.dao;

import com.github.copilot.configcenter.entity.ConfigDO;
import java.util.List;

/**
 * Interface for data access operations related to {@link ConfigDO} entities.
 */
public interface ConfigDAO {
    /**
     * Inserts a new {@link ConfigDO} into the database.
     *
     * @param configDO The configuration object to be inserted.
     * @return The generated ID for the inserted configuration.
     */
    long insertConfigDO(ConfigDO configDO);

    /**
     * Updates an existing {@link ConfigDO} in the database.
     *
     * @param configDO The configuration object with updated fields.
     */
    void updateConfig(ConfigDO configDO);

    /**
     * Deletes a {@link ConfigDO} from the database by its ID.
     *
     * @param id The ID of the configuration to be deleted.
     */
    void delConfig(long id);

    /**
     * Retrieves a {@link ConfigDO} from the database by its ID.
     *
     * @param id The ID of the configuration to retrieve.
     * @return The retrieved configuration object, or {@code null} if not found.
     */
    ConfigDO getConfig(long id);

    /**
     * Retrieves all {@link ConfigDO} entities from the database.
     *
     * @return A list of all configuration objects.
     */
    List<ConfigDO> getAllConfig();
}