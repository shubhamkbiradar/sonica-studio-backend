package com.project.sonica.aop;

import java.util.concurrent.TimeUnit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import com.project.sonica.aop.annotations.NoLog;
import com.project.sonica.aop.util.SafeLog;

@Aspect
@Component
@Order(0)
public class LoggingAspect {
	@Value("${sonica.aop.logging.enabled:true}")
	private boolean enabled;

	@Value("${sonica.aop.logging.log-args:false}")
	private boolean logArgs;

	@Value("${sonica.aop.logging.log-result:false}")
	private boolean logResult;

	@Value("${sonica.aop.logging.slow-ms:750}")
	private long slowMs;

	@Pointcut("within(com.project.sonica..*) && (within(com.project.sonica.controller..*) || within(com.project.sonica.service..*) || @within(org.springframework.web.bind.annotation.RestController))")
	public void appMethods() {
	}

	@Pointcut("@annotation(com.project.sonica.aop.annotations.NoLog) || @within(com.project.sonica.aop.annotations.NoLog)")
	public void noLog() {
	}

	@Around("appMethods() && !noLog()")
	public Object logAround(ProceedingJoinPoint pjp) throws Throwable {
		if (!enabled) {
			return pjp.proceed();
		}

		Class<?> targetType = pjp.getTarget() == null ? pjp.getSignature().getDeclaringType() : pjp.getTarget().getClass();
		Logger log = LoggerFactory.getLogger(targetType);

		MethodSignature sig = (MethodSignature) pjp.getSignature();
		String method = sig.getDeclaringType().getSimpleName() + "." + sig.getName();
		boolean isController = targetType.getName().contains(".controller.") || sig.getDeclaringType().isAnnotationPresent(RestController.class);

		long startNs = System.nanoTime();
		if (isController ? log.isInfoEnabled() : log.isDebugEnabled()) {
			if (logArgs) {
				Object[] args = pjp.getArgs();
				String[] safeArgs = new String[args == null ? 0 : args.length];
				for (int i = 0; i < safeArgs.length; i++) {
					safeArgs[i] = SafeLog.value(args[i]);
				}
				if (isController) {
					log.info("-> {} args={}", method, safeArgs);
				} else {
					log.debug("-> {} args={}", method, safeArgs);
				}
			} else {
				if (isController) {
					log.info("-> {}", method);
				} else {
					log.debug("-> {}", method);
				}
			}
		}

		try {
			Object result = pjp.proceed();
			long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

			if (tookMs >= slowMs) {
				log.warn("<- {} took={}ms (slow)", method, tookMs);
			} else if (isController ? log.isInfoEnabled() : log.isDebugEnabled()) {
				if (logResult) {
					if (isController) {
						log.info("<- {} took={}ms result={}", method, tookMs, SafeLog.value(result));
					} else {
						log.debug("<- {} took={}ms result={}", method, tookMs, SafeLog.value(result));
					}
				} else {
					if (isController) {
						log.info("<- {} took={}ms", method, tookMs);
					} else {
						log.debug("<- {} took={}ms", method, tookMs);
					}
				}
			}
			return result;
		} catch (Throwable t) {
			long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
			log.error("<- {} took={}ms threw={}: {}", method, tookMs, t.getClass().getSimpleName(), SafeLog.value(t.getMessage()));
			throw t;
		}
	}
}
