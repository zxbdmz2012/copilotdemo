package com.github.copilot.configcenter.server.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.copilot.configcenter.common.model.ConfigVO;
import com.github.copilot.configcenter.common.model.Result;
import com.github.copilot.configcenter.server.model.ConfigBO;
import com.github.copilot.configcenter.server.model.ConfigPolingTask;
import com.github.copilot.configcenter.server.service.ConfigService;
import com.github.copilot.configcenter.server.service.impl.ConfigServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


// Defines a REST controller for handling configuration-related requests.
@RestController
// Maps requests starting with "/config" to methods in this controller.
@RequestMapping("/config")
public class ConfigController {

    // Injects an instance of ConfigService to interact with configuration data.
    @Autowired
    private ConfigService configService;

    // Handles POST requests to "/config/insert" for inserting new configurations.
    @PostMapping("/insert")
    public Result<Void> insertConfig(@RequestBody ConfigVO configVO) {
        // Validates the configuration object and checks for operation permission.
        Result<ConfigBO> result = checkOpConfig(configVO);
        // If validation fails, returns a failure result.
        if (result.failed()) {
            return Result.resultToFail(result);
        }
        // Inserts the configuration and returns the result.
        return configService.insertConfig(result.getData());
    }

    // Handles POST requests to "/config/update" for updating existing configurations.
    @PostMapping("/update")
    public Result<Void> updateConfig(@RequestBody ConfigVO configVO) {
        // Validates the configuration object and checks for operation permission.
        Result<ConfigBO> result = checkOpConfig(configVO);
        // If validation fails, returns a failure result.
        if (result.failed()) {
            return Result.resultToFail(result);
        }
        // Prepares the configuration object for update.
        ConfigBO configBO = result.getData();
        long id = configVO.getId();
        configBO.setId(id);
        // Updates the configuration and returns the result.
        return configService.updateConfig(configBO);
    }

    // Handles POST requests to "/config/delete" for deleting configurations by ID.
    @PostMapping("/delete")
    public Result<Void> delConfig(@RequestParam Long id) {
        // Deletes the configuration by ID and returns the result.
        return configService.delConfig(id);
    }

    // Handles GET requests to "/config/get" for retrieving all valid configurations.
    @GetMapping("/get")
    public Result<List<ConfigVO>> getAllValidConfig() {
        // Retrieves all valid configurations.
        Result<List<ConfigBO>> result = configService.getAllValidConfig();
        // If retrieval fails, returns a failure result.
        if (result.failed()) {
            return Result.resultToFail(result);
        }
        // Converts and returns the configurations as a list of ConfigVO objects.
        return Result.success(result.getData().stream().map(ConfigServiceImpl::configBO2ConfigVO).collect(Collectors.toList()));
    }

    // Handles POST requests to "/config/change/get" for retrieving configurations that have changed.
    @PostMapping("/change/get")
    public Result<List<ConfigVO>> getChangeConfig(@RequestBody Map<Long, Integer> configIdMap) {
        // Validates the input map for configuration IDs and versions.
        if (configIdMap == null || configIdMap.isEmpty()) {
            return Result.fail("配置参数错误");
        }
        // Retrieves all valid configurations.
        Result<List<ConfigBO>> result = configService.getAllValidConfig();
        // If retrieval fails, returns a failure result.
        if (result.failed()) {
            return Result.resultToFail(result);
        }
        return Result.success(result.getData().stream()
                .filter(c -> configIdMap.containsKey(c.getId()))
                .filter(c -> c.getVersion() > configIdMap.get(c.getId()))
                .map(ConfigServiceImpl::configBO2ConfigVO).collect(Collectors.toList()));
    }

    // Validates the input configuration VO object and checks for necessary conditions.
    private Result<ConfigBO> checkOpConfig(ConfigVO configVO) {
        // Extracts the name from the configuration VO and trims it for whitespace.
        String name = configVO.getName();
        // Checks if the name is null or empty after trimming. If so, returns a failure result.
        if (name == null || (name = name.trim()).length() == 0) {
            return Result.fail("配置名不能为空");
        }
        // Retrieves the configuration data from the configuration VO.
        JSONObject configData = configVO.getConfigData();
        // Checks if the configuration data is null. If so, returns a failure result.
        if (configData == null) {
            return Result.fail("配置内容不能为空");
        }
        // Creates a new ConfigBO object and sets its name and configuration data.
        ConfigBO configBO = new ConfigBO();
        configBO.setName(name);
        configBO.setConfigData(configData);
        // Returns a success result with the populated ConfigBO object.
        return Result.success(configBO);
    }

    // Handles long polling requests for configuration changes.
    @PostMapping("/change/get/long")
    public Result<Void> getLongChangeConfig(@RequestBody Map<Long, Integer> configIdMap, HttpServletRequest request, HttpServletResponse response) {
        // Validates the input map for configuration IDs and versions.
        if (configIdMap == null || configIdMap.isEmpty()) {
            // Returns a failure result if the input map is null or empty.
            return Result.fail("配置参数错误");
        }
        // Sets the response content type to JSON with UTF-8 encoding.
        response.setContentType("application/json;charset=UTF-8");

        // Starts an asynchronous context for the request.
        AsyncContext asyncContext = request.startAsync();
        // Sets the timeout for the asynchronous context to never timeout.
        asyncContext.setTimeout(0);

        // Creates a new ConfigPolingTask object for handling the long polling.
        ConfigPolingTask configPolingTask = new ConfigPolingTask();
        // Sets the asynchronous context in the polling task.
        configPolingTask.setAsyncContext(asyncContext);
        // Sets the configuration polling data map in the polling task.
        configPolingTask.setConfigPolingDataMap(configIdMap);
        // Sets the end time for the polling task (28 seconds from now).
        configPolingTask.setEndTime(System.currentTimeMillis() + 28 * 1000);
        // Registers the polling task with the configuration service for listening to configuration changes.
        configService.configListener(configPolingTask);
        // Returns null as the method does not directly return a result but relies on asynchronous processing.
        return null;
    }
}
