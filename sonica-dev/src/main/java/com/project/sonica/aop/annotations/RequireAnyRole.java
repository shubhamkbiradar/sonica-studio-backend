package com.project.sonica.aop.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Requires the caller to have at least one of the provided roles.
 *
 * Roles should be specified without the "ROLE_" prefix, e.g. "ADMIN".
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireAnyRole {
	String[] value();
}

