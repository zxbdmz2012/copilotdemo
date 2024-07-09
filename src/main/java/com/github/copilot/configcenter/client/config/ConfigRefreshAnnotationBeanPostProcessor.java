package com.github.copilot.configcenter.client.config;


import com.github.copilot.configcenter.client.ConfigCenterClient;
import com.github.copilot.configcenter.client.annotation.ConfigRefresh;
import com.github.copilot.configcenter.client.annotation.EnableConfigClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.util.ReflectionUtils;


@Slf4j
public class ConfigRefreshAnnotationBeanPostProcessor implements ApplicationRunner, BeanPostProcessor, BeanFactoryAware, EnvironmentAware, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
    private Environment environment;

    private ConfigurableBeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {

        if (!(beanFactory instanceof ConfigurableBeanFactory)) {
            log.warn("ConfigurableBeanFactory requires a ConfigurableListableBeanFactory");
            return;
        }
        this.beanFactory = (ConfigurableBeanFactory) beanFactory;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, final String beanName) throws BeansException {
        if (beanFactory != null) {
            ReflectionUtils.doWithFields(bean.getClass(), field -> {
                try {
                    ConfigRefresh configRefresh = AnnotationUtils.getAnnotation(field, ConfigRefresh.class);
                    if (configRefresh == null) {
                        return;
                    }
                    Value valueAnnotation = AnnotationUtils.getAnnotation(field, Value.class);
                    if (valueAnnotation == null) {
                        return;
                    }
                    String value = valueAnnotation.value();
                    String relValue = beanFactory.resolveEmbeddedValue(value);

                    ConfigCenterClient configCenterClient = ConfigCenterClient.getInstance(null);
                    configCenterClient.addRefreshFieldValue(bean, field, relValue);
                } catch (Exception e) {
                    log.error("set bean field fail,beanName:{},fieldName:{}", bean.getClass().getName(), field.getName(), e);
                }
            });

            ConfigRefresh configRefresh = AnnotationUtils.findAnnotation(bean.getClass(), ConfigRefresh.class);
            if (configRefresh != null) {
                ConfigurationProperties configurationProperties = AnnotationUtils.findAnnotation(bean.getClass(), ConfigurationProperties.class);
                if (configurationProperties != null) {
                    ConfigCenterClient configCenterClient = ConfigCenterClient.getInstance(null);
                    configCenterClient.addRefreshBeanList(bean);
                }
            }
        }
        return bean;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void run(ApplicationArguments args) {
        boolean isConfigClientEnabled = applicationContext.getBeansWithAnnotation(EnableConfigClient.class).size() > 0;

        if (isConfigClientEnabled) {
            ConfigCenterClient configCenterClient = ConfigCenterClient.getInstance(null);
            configCenterClient.startSpringBootLongPolling((ConfigurableEnvironment) environment, beanFactory);
        }
    }
}
