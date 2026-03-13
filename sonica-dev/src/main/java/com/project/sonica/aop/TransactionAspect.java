package com.project.sonica.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.project.sonica.aop.annotations.SonicaTx;

/**
 * Transaction management via Spring AOP, driven by {@link SonicaTx}.
 *
 * Spring Data repositories already use transactions internally; this aspect exists so service
 * methods can define a clear atomic boundary spanning multiple repository calls.
 */
@Aspect
@Component
@Order(20)
public class TransactionAspect {
	private static final Logger log = LoggerFactory.getLogger(TransactionAspect.class);

	private final PlatformTransactionManager txManager;

	public TransactionAspect(PlatformTransactionManager txManager) {
		this.txManager = txManager;
	}

	@Around("@annotation(tx)")
	public Object inTxMethod(ProceedingJoinPoint pjp, SonicaTx tx) throws Throwable {
		return proceedInTx(pjp, tx);
	}

	@Around("@within(tx)")
	public Object inTxType(ProceedingJoinPoint pjp, SonicaTx tx) throws Throwable {
		return proceedInTx(pjp, tx);
	}

	private Object proceedInTx(ProceedingJoinPoint pjp, SonicaTx tx) throws Throwable {
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setReadOnly(tx.readOnly());
		def.setPropagationBehavior(toPropagationBehavior(tx.propagation()));
		if (tx.timeoutSeconds() >= 0) {
			def.setTimeout(tx.timeoutSeconds());
		}

		TransactionStatus status = txManager.getTransaction(def);
		try {
			Object result = pjp.proceed();
			txManager.commit(status);
			return result;
		} catch (Throwable t) {
			boolean rollback = shouldRollback(t, tx);
			if (rollback) {
				txManager.rollback(status);
			} else {
				// Mirrors Spring's default: checked exceptions don't necessarily roll back.
				txManager.commit(status);
			}

			MethodSignature sig = (MethodSignature) pjp.getSignature();
			log.debug("Tx {}.{} completed with rollback={} dueTo={}", sig.getDeclaringType().getSimpleName(), sig.getName(),
					rollback, t.getClass().getSimpleName());
			throw t;
		}
	}

	private static int toPropagationBehavior(Propagation propagation) {
		if (propagation == null) {
			return TransactionDefinition.PROPAGATION_REQUIRED;
		}
		return switch (propagation) {
		case REQUIRED -> TransactionDefinition.PROPAGATION_REQUIRED;
		case REQUIRES_NEW -> TransactionDefinition.PROPAGATION_REQUIRES_NEW;
		case SUPPORTS -> TransactionDefinition.PROPAGATION_SUPPORTS;
		case MANDATORY -> TransactionDefinition.PROPAGATION_MANDATORY;
		case NEVER -> TransactionDefinition.PROPAGATION_NEVER;
		case NOT_SUPPORTED -> TransactionDefinition.PROPAGATION_NOT_SUPPORTED;
		case NESTED -> TransactionDefinition.PROPAGATION_NESTED;
		};
	}

	private static boolean shouldRollback(Throwable t, SonicaTx tx) {
		for (Class<? extends Throwable> noRb : tx.noRollbackOn()) {
			if (noRb != null && noRb.isInstance(t)) {
				return false;
			}
		}

		Class<? extends Throwable>[] rb = tx.rollbackOn();
		if (rb != null && rb.length > 0) {
			for (Class<? extends Throwable> rbOn : rb) {
				if (rbOn != null && rbOn.isInstance(t)) {
					return true;
				}
			}
			return false;
		}

		// Default behavior matches Spring: rollback on unchecked exceptions and errors.
		return (t instanceof RuntimeException) || (t instanceof Error);
	}
}

