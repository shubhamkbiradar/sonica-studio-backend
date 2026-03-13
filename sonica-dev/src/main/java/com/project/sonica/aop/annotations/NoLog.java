package com.project.sonica.aop.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class/method as excluded from {@link com.project.sonica.aop.LoggingAspect}.
 *
 * Use this on endpoints like login/refresh, or methods that handle secrets.
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface NoLog {
}

