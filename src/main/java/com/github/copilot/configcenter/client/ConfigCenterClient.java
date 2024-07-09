package com.github.copilot.configcenter.client;

import com.alibaba.fastjson.JSON;
import com.github.copilot.configcenter.client.model.*;
import com.github.copilot.configcenter.client.utils.DataTransUtil;
import com.github.copilot.configcenter.client.utils.HttpUtil;
import com.github.copilot.configcenter.common.model.ConfigVO;
import com.github.copilot.configcenter.common.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j
public class ConfigCenterClient {

    // Constant for the property source name
    public static final String PROPERTY_SOURCE_NAME = "configCenter";
    // Singleton instance of ConfigCenterClient
    private static volatile ConfigCenterClient client;
    // Server URL
    private final String url;
    // Map to store configurations with their IDs
    private final Map<Long, ConfigBO> configMap;
    // List of beans to be refreshed
    private final List<Object> refreshBeanList;
    // List of fields and their new values for refresh
    private final List<RefreshFieldValueBO> refreshFieldValueList;

    // Private constructor to initialize the client
    private ConfigCenterClient(String url) {
        this.url = url;
        this.refreshBeanList = new ArrayList<>();
        this.refreshFieldValueList = new ArrayList<>();
        // Converts configuration from the config center to properties format
        List<ConfigVO> configList = getAllValidConfig();
        this.configMap = configList2ConfigMap(configList);
    }

    // Singleton pattern to get the instance of ConfigCenterClient
    public static ConfigCenterClient getInstance(String url) {
        return Optional.ofNullable(client).orElseGet(() -> {
            synchronized (ConfigCenterClient.class) {
                if (client == null) {
                    client = new ConfigCenterClient(url);
                }
                return client;
            }
        });
    }

    // Retrieves all valid configurations from the server
    public List<ConfigVO> getAllValidConfig() {
        HttpRespBO httpRespBO = HttpUtil.httpGet(url + "/config/get");
        return httpResp2ConfigVOList(httpRespBO);
    }

    // Adds a field to be refreshed for a specific configuration key
    public void addRefreshField(String key, RefreshFieldBO refreshFieldBO) {
        configMap.values().stream().map(ConfigBO::getConfigDataList).filter(Objects::nonNull)
                .flatMap(List::stream).filter(configDataBO -> configDataBO.getKey().equals(key))
                .findFirst().ifPresent(configDataBO -> configDataBO.addRefreshField(refreshFieldBO));
    }

    // Adds a new value for a specific field to be refreshed
    public void addRefreshFieldValue(Object bean, Field field, String value) {
        refreshFieldValueList.add(new RefreshFieldValueBO(bean, field, value));
    }

    // Adds a bean to the list of beans to be refreshed
    public void addRefreshBeanList(Object bean) {
        refreshBeanList.add(bean);
    }

    // Retrieves the current configuration properties
    public Map<String, Object> getConfigProperty() {
        return configMap.values().stream().map(ConfigBO::getConfigDataList).filter(Objects::nonNull)
                .flatMap(List::stream).collect(Collectors.toMap(ConfigDataBO::getKey, ConfigDataBO::getValue, (k1, k2) -> k1));
    }

    // Starts short polling for configuration changes
    public void startShortPolling() {
        polling("/config/change/get", () -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, 2000);
    }

    // Starts long polling for configuration changes
    public void startLongPolling() {
        polling("/config/change/get/long", null, 30000);
    }

    // Generic polling method for both short and long polling
    public void polling(String uri, Runnable runnable, int readTimeout) {
        Thread thread = new Thread(() -> {
            while (!Thread.interrupted()) {
                try {
                    Optional.ofNullable(runnable).ifPresent(Runnable::run);
                    Map<Long, List<ConfigDataBO>> refreshConfigMap = new HashMap<>();
                    configMap.values().forEach(configBO -> {
                        Optional.ofNullable(configBO.getConfigDataList()).ifPresent(cdList -> cdList.stream()
                                .filter(cd -> cd.getRefreshFieldList() != null && !cd.getRefreshFieldList().isEmpty())
                                .forEach(refreshConfigMap.computeIfAbsent(configBO.getId(), k1 -> new ArrayList<>())::add));
                    });
                    if (refreshConfigMap.isEmpty()) {
                        return;
                    }
                    Map<String, Integer> configIdMap = refreshConfigMap.keySet().stream()
                            .collect(Collectors.toMap(String::valueOf, configId -> configMap.get(configId).getVersion()));
                    HttpRespBO httpRespBO = HttpUtil.httpPostJson(url + uri, JSON.toJSONString(configIdMap), readTimeout);
                    List<ConfigVO> configList = httpResp2ConfigVOList(httpRespBO);
                    if (configList.isEmpty()) {
                        continue;
                    }
                    configList.forEach(configVO -> {
                        Map<String, Object> result = new HashMap<>();
                        DataTransUtil.buildFlattenedMap(result, configVO.getConfigData(), "");
                        ConfigBO configBO = this.configMap.get(configVO.getId());
                        configBO.setVersion(configVO.getVersion());

                        List<ConfigDataBO> configDataList = configBO.getConfigDataList();
                        Map<String, ConfigDataBO> configDataMap = configDataList.stream()
                                .collect(Collectors.toMap(ConfigDataBO::getKey, Function.identity()));
                        result.forEach((key, value) -> {
                            ConfigDataBO configDataBO = configDataMap.get(key);
                            if (configDataBO == null) {
                                configDataList.add(new ConfigDataBO(key, value.toString()));
                            } else {
                                configDataBO.setValue(value.toString());
                                List<RefreshFieldBO> refreshFieldList = configDataBO.getRefreshFieldList();
                                if (refreshFieldList == null) {
                                    refreshFieldList = new ArrayList<>();
                                    configDataBO.setRefreshFieldList(refreshFieldList);
                                }
                                refreshFieldList.forEach(refreshFieldBO -> {
                                    try {
                                        Field field = refreshFieldBO.getField();
                                        field.setAccessible(true);
                                        field.set(refreshFieldBO.getBean(), value.toString());
                                    } catch (Exception e) {
                                        log.error("startShortPolling set Field error", e);
                                    }
                                });
                            }
                        });

                    });
                } catch (Exception e) {
                    log.error("startShortPolling error", e);
                }
            }
        });
        thread.setName("startShortPolling");
        thread.setDaemon(true);
        thread.start();
    }

    // Starts long polling specifically designed for Spring Boot applications
    public void startSpringBootLongPolling(ConfigurableEnvironment environment, ConfigurableBeanFactory beanFactory) {
        if (configMap.isEmpty() || refreshFieldValueList.isEmpty()) {
            log.info("configMap.size:{} refreshFieldValueList.size:{}", configMap.size(), refreshFieldValueList.size());
            return;
        }
        MutablePropertySources propertySources = environment.getPropertySources();
        MapPropertySource configCenter = (MapPropertySource) propertySources.get(PROPERTY_SOURCE_NAME);
        if (configCenter == null) {
            log.warn("configCenter is null");
            return;
        }
        Map<String, Object> source = configCenter.getSource();
        Thread thread = new Thread(() -> {
            while (!Thread.interrupted()) {
                try {
                    Map<String, Integer> configIdMap = configMap.values().stream().collect(Collectors.toMap(c -> c.getId() + "", ConfigBO::getVersion));
                    HttpRespBO httpRespBO = HttpUtil.httpPostJson(url + "/config/change/get/long", JSON.toJSONString(configIdMap), 30000);
                    List<ConfigVO> configList = httpResp2ConfigVOList(httpRespBO);
                    if (configList.isEmpty()) {
                        continue;
                    }
                    configList.forEach(configVO -> {
                        Map<String, Object> result = new HashMap<>();
                        DataTransUtil.buildFlattenedMap(result, configVO.getConfigData(), "");
                        ConfigBO configBO = this.configMap.get(configVO.getId());
                        configBO.setVersion(configVO.getVersion());

                        List<ConfigDataBO> configDataList = configBO.getConfigDataList();
                        Map<String, ConfigDataBO> configDataMap = configDataList.stream()
                                .collect(Collectors.toMap(ConfigDataBO::getKey, Function.identity()));
                        result.forEach((key, value) -> {
                            ConfigDataBO configDataBO = configDataMap.get(key);
                            if (configDataBO == null) {
                                configDataList.add(new ConfigDataBO(key, value.toString()));
                            } else {
                                configDataBO.setValue(value.toString());
                                source.put(key, value);
                            }
                        });
                    });

                    refreshFieldValueList.forEach(refreshFieldBO -> {
                        try {
                            Field field = refreshFieldBO.getField();
                            Value valueAnnotation = AnnotationUtils.getAnnotation(field, Value.class);
                            if (valueAnnotation == null) {
                                return;
                            }
                            String value = valueAnnotation.value();
                            String relValue = beanFactory.resolveEmbeddedValue(value);
                            if (relValue.equals(refreshFieldBO.getValue())) {
                                return;
                            }
                            field.setAccessible(true);
                            field.set(refreshFieldBO.getBean(), relValue);
                        } catch (Exception e) {
                            log.error("startSpringBootLongPolling set Field error", e);
                        }
                    });

                    refreshBeanList.forEach(refreshBean -> {
                        ConfigurationProperties configurationProperties = AnnotationUtils.findAnnotation(refreshBean.getClass(), ConfigurationProperties.class);
                        if (configurationProperties == null) {
                            log.warn("refreshBeanList refreshBean configurationProperties is null, class:{}", refreshBean.getClass());
                            return;
                        }
                        Binder binder = Binder.get(environment);
                        binder.bind(configurationProperties.prefix(), Bindable.ofInstance(refreshBean));
                    });
                } catch (Exception e) {
                    log.error("startSpringBootLongPolling error", e);
                }
            }
        });
        thread.setName("startSpringBootLongPolling");
        thread.setDaemon(true);
        thread.start();
    }

    private Map<Long, ConfigBO> configList2ConfigMap(List<ConfigVO> configList) {
        return Optional.ofNullable(configList).map(list -> list.stream().map(configVO -> {
            Map<String, Object> result = new HashMap<>();
            DataTransUtil.buildFlattenedMap(result, configVO.getConfigData(), "");

            ConfigBO configBO = new ConfigBO();
            configBO.setId(configVO.getId());
            configBO.setVersion(configVO.getVersion());
            configBO.setConfigDataList(result.entrySet().stream().map(e ->
                    new ConfigDataBO(e.getKey(), e.getValue().toString())).collect(Collectors.toList()));
            return configBO;
        }).collect(Collectors.toMap(ConfigBO::getId, Function.identity(), (k1, k2) -> k1))).orElseGet(HashMap::new);
    }

    /**
     * Converts an HTTP response into a list of ConfigVO objects.
     * This method first checks if the HTTP response indicates success (status code 200) and if the body is not null.
     * It then parses the body into a Result object to check if the operation was successful.
     * Finally, it converts the data part of the Result object into a list of ConfigVO objects.
     *
     * @param httpRespBO The HTTP response object to be converted.
     * @return A list of ConfigVO objects extracted from the HTTP response.
     * @throws IllegalArgumentException If the HTTP response indicates failure or if the body is null.
     */
    private List<ConfigVO> httpResp2ConfigVOList(HttpRespBO httpRespBO) {
        // Check if the HTTP response is successful (status code 200)
        if (!httpRespBO.success()) {
            throw new IllegalArgumentException("Failed to get configuration: code:" + httpRespBO.getCode() + ",msg:" + httpRespBO.getMessage());
        }
        // Check if the HTTP response body is null
        if (httpRespBO.getBody() == null) {
            throw new IllegalArgumentException("Failed to get configuration, body is null: code:" + httpRespBO.getCode() + ",msg:" + httpRespBO.getMessage());
        }
        // Parse the HTTP response body into a Result object
        Result<?> result = JSON.parseObject(new String(httpRespBO.getBody(), StandardCharsets.UTF_8), Result.class);
        // Check if the operation indicated by the Result object was unsuccessful
        if (result.failed()) {
            return new ArrayList<>();
        }
        // Convert the data part of the Result object into a list of ConfigVO objects and return it
        return JSON.parseArray(JSON.toJSONString(result.getData()), ConfigVO.class);

    }
}
