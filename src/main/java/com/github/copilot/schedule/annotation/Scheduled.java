package com.github.copilot.schedule.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Scheduled {
    String cron() default "";

    String hour() default "";

    String min() default "";

    String sec() default "";

    String timezone() default "";

    String jobType() default "";

    String rate() default "";

    String cycle() default "";


}
