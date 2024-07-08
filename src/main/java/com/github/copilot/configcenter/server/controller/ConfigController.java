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


@RestController
@RequestMapping("/config")
public class ConfigController {

    @Autowired
    private ConfigService configService;

    @PostMapping("/insert")
    public Result<Void> insertConfig(@RequestBody ConfigVO configVO) {
        Result<ConfigBO> result = checkOpConfig(configVO);
        if (result.failed()) {
            return Result.resultToFail(result);
        }
        return configService.insertConfig(result.getData());
    }

    @PostMapping("/update")
    public Result<Void> updateConfig(@RequestBody ConfigVO configVO) {
        Result<ConfigBO> result = checkOpConfig(configVO);
        if (result.failed()) {
            return Result.resultToFail(result);
        }
        ConfigBO configBO = result.getData();
        long id = configVO.getId();
        configBO.setId(id);
        return configService.updateConfig(configBO);
    }

    @PostMapping("/delete")
    public Result<Void> delConfig(@RequestParam Long id) {
        return configService.delConfig(id);
    }

    @GetMapping("/get")
    public Result<List<ConfigVO>> getAllValidConfig() {
        Result<List<ConfigBO>> result = configService.getAllValidConfig();
        if (result.failed()) {
            return Result.resultToFail(result);
        }
        return Result.success(result.getData().stream().map(ConfigServiceImpl::configBO2ConfigVO).collect(Collectors.toList()));
    }

    @PostMapping("/change/get")
    public Result<List<ConfigVO>> getChangeConfig(@RequestBody Map<Long, Integer> configIdMap) {
        if (configIdMap == null || configIdMap.isEmpty()) {
            return Result.fail("配置参数错误");
        }
        Result<List<ConfigBO>> result = configService.getAllValidConfig();
        if (result.failed()) {
            return Result.resultToFail(result);
        }
        return Result.success(result.getData().stream()
                .filter(c -> configIdMap.containsKey(c.getId()))
                .filter(c -> c.getVersion() > configIdMap.get(c.getId()))
                .map(ConfigServiceImpl::configBO2ConfigVO).collect(Collectors.toList()));
    }

    @PostMapping("/change/get/long")
    public Result<Void> getLongChangeConfig(@RequestBody Map<Long, Integer> configIdMap, HttpServletRequest request, HttpServletResponse response) {
        if (configIdMap == null || configIdMap.isEmpty()) {
            return Result.fail("配置参数错误");
        }
        response.setContentType("application/json;charset=UTF-8");

        AsyncContext asyncContext = request.startAsync();
        asyncContext.setTimeout(0);

        ConfigPolingTask configPolingTask = new ConfigPolingTask();
        configPolingTask.setAsyncContext(asyncContext);
        configPolingTask.setConfigPolingDataMap(configIdMap);
        configPolingTask.setEndTime(System.currentTimeMillis() + 28 * 1000);
        configService.configListener(configPolingTask);
        return null;
    }

    private Result<ConfigBO> checkOpConfig(ConfigVO configVO) {
        String name = configVO.getName();
        if (name == null || (name = name.trim()).length() == 0) {
            return Result.fail("配置名不能为空");
        }
        JSONObject configData = configVO.getConfigData();
        if (configData == null) {
            return Result.fail("配置内容不能为空");
        }
        ConfigBO configBO = new ConfigBO();
        configBO.setName(name);
        configBO.setConfigData(configData);
        return Result.success(configBO);
    }
}
