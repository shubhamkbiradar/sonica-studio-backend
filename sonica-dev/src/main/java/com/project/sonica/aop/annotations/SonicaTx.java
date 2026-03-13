package com.project.sonica.aop.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.transaction.annotation.Propagation;

/**
 * Transaction boundary applied via Spring AOP.
 *
 * This is intentionally small and explicit. Prefer annotating service methods that perform
 * multiple writes or need atomic behavior.
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface SonicaTx {
	boolean readOnly() default false;

	Propagation propagation() default Propagation.REQUIRED;

	/**
	 * Transaction timeout in seconds. Use -1 for default.
	 */
	int timeoutSeconds() default -1;

	/**
	 * Additional exceptions that should trigger rollback (including checked exceptions).
	 *
	 * If empty, default rollback behavior is used (rollback on RuntimeException and Error).
	 */
	Class<? extends Throwable>[] rollbackOn() default {};

	/**
	 * Exceptions that should not trigger rollback.
	 */
	Class<? extends Throwable>[] noRollbackOn() default {};
}

