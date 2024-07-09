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

// Enable logging for this class
@Slf4j
// The class implements multiple interfaces to interact with the Spring lifecycle and beans
public class ConfigRefreshAnnotationBeanPostProcessor implements ApplicationRunner, BeanPostProcessor, BeanFactoryAware, EnvironmentAware, ApplicationContextAware {

    // Field to hold the application context
    private ApplicationContext applicationContext;

    // Setter method for injecting the application context
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
    // Field to hold the environment
    private Environment environment;

    // Field to hold the bean factory
    private ConfigurableBeanFactory beanFactory;

    // Setter method for injecting the bean factory
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        // Check if the bean factory is of the required type
        if (!(beanFactory instanceof ConfigurableBeanFactory)) {
            // Log a warning if not
            log.warn("ConfigurableBeanFactory requires a ConfigurableListableBeanFactory");
            return;
        }
        this.beanFactory = (ConfigurableBeanFactory) beanFactory;
    }

    // Method to process beans before their initialization
    @Override
    public Object postProcessBeforeInitialization(Object bean, final String beanName) throws BeansException {
        // Check if the bean factory has been set
        if (beanFactory != null) {
            // Use reflection to process all fields of the bean
            ReflectionUtils.doWithFields(bean.getClass(), field -> {
                try {
                    // Check if the field is annotated with ConfigRefresh
                    ConfigRefresh configRefresh = AnnotationUtils.getAnnotation(field, ConfigRefresh.class);
                    if (configRefresh == null) {
                        return;
                    }
                    // Check if the field is annotated with Value
                    Value valueAnnotation = AnnotationUtils.getAnnotation(field, Value.class);
                    if (valueAnnotation == null) {
                        return;
                    }
                    // Resolve the value of the annotation
                    String value = valueAnnotation.value();
                    String relValue = beanFactory.resolveEmbeddedValue(value);

                    // Get an instance of the ConfigCenterClient
                    ConfigCenterClient configCenterClient = ConfigCenterClient.getInstance(null);
                    // Register the field for dynamic refresh
                    configCenterClient.addRefreshFieldValue(bean, field, relValue);
                } catch (Exception e) {
                    // Log any errors during field processing
                    log.error("set bean field fail,beanName:{},fieldName:{}", bean.getClass().getName(), field.getName(), e);
                }
            });

            // Check if the bean is annotated with ConfigRefresh
            ConfigRefresh configRefresh = AnnotationUtils.findAnnotation(bean.getClass(), ConfigRefresh.class);
            if (configRefresh != null) {
                // Check if the bean is annotated with ConfigurationProperties
                ConfigurationProperties configurationProperties = AnnotationUtils.findAnnotation(bean.getClass(), ConfigurationProperties.class);
                if (configurationProperties != null) {
                    // Get an instance of the ConfigCenterClient
                    ConfigCenterClient configCenterClient = ConfigCenterClient.getInstance(null);
                    // Register the bean for dynamic refresh
                    configCenterClient.addRefreshBeanList(bean);
                }
            }
        }
        return bean;
    }

    // Setter method for injecting the environment
    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    // Method to perform actions after the application has started
    @Override
    public void run(ApplicationArguments args) {
        // Check if the EnableConfigClient annotation is present in the application context
        boolean isConfigClientEnabled = applicationContext.getBeansWithAnnotation(EnableConfigClient.class).size() > 0;

        // If enabled, start the long-polling process for configuration changes
        if (isConfigClientEnabled) {
            ConfigCenterClient configCenterClient = ConfigCenterClient.getInstance(null);
            configCenterClient.startSpringBootLongPolling((ConfigurableEnvironment) environment, beanFactory);
        }
    }
}
