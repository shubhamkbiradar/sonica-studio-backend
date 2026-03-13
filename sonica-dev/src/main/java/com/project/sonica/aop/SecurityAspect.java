package com.project.sonica.aop;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.project.sonica.aop.annotations.RequireAnyRole;
import com.project.sonica.aop.annotations.RequireAuthenticated;
import com.project.sonica.aop.annotations.RequireRole;

/**
 * Simple, readable security checks via Spring AOP.
 *
 * Note: For complex conditions, keep using {@code @PreAuthorize}.
 */
@Aspect
@Component
@Order(10)
public class SecurityAspect {
	private static final Logger log = LoggerFactory.getLogger(SecurityAspect.class);

	@Pointcut("@annotation(com.project.sonica.aop.annotations.RequireAuthenticated) || @within(com.project.sonica.aop.annotations.RequireAuthenticated)")
	public void requireAuthenticated() {
	}

	@Pointcut("@annotation(com.project.sonica.aop.annotations.RequireRole) || @within(com.project.sonica.aop.annotations.RequireRole)")
	public void requireRole() {
	}

	@Pointcut("@annotation(com.project.sonica.aop.annotations.RequireAnyRole) || @within(com.project.sonica.aop.annotations.RequireAnyRole)")
	public void requireAnyRole() {
	}

	@Before("requireAuthenticated() && @annotation(req)")
	public void checkAuthenticatedMethod(JoinPoint jp, RequireAuthenticated req) {
		ensureAuthenticated(jp);
	}

	@Before("requireAuthenticated() && @within(req)")
	public void checkAuthenticatedType(JoinPoint jp, RequireAuthenticated req) {
		ensureAuthenticated(jp);
	}

	@Before("requireRole() && @annotation(req)")
	public void checkRoleMethod(JoinPoint jp, RequireRole req) {
		ensureHasAnyRole(jp, req.value());
	}

	@Before("requireRole() && @within(req)")
	public void checkRoleType(JoinPoint jp, RequireRole req) {
		ensureHasAnyRole(jp, req.value());
	}

	@Before("requireAnyRole() && @annotation(req)")
	public void checkAnyRoleMethod(JoinPoint jp, RequireAnyRole req) {
		ensureHasAnyRole(jp, req.value());
	}

	@Before("requireAnyRole() && @within(req)")
	public void checkAnyRoleType(JoinPoint jp, RequireAnyRole req) {
		ensureHasAnyRole(jp, req.value());
	}

	private void ensureAuthenticated(JoinPoint jp) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated()) {
			denyUnauthenticated(jp);
		}
	}

	private void ensureHasAnyRole(JoinPoint jp, String... roles) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated()) {
			denyUnauthenticated(jp);
		}

		Set<String> want = new HashSet<>();
		for (String r : roles) {
			if (r == null || r.isBlank()) {
				continue;
			}
			want.add(toAuthority(r));
		}

		Set<String> have = new HashSet<>();
		for (GrantedAuthority a : auth.getAuthorities()) {
			if (a != null && a.getAuthority() != null) {
				have.add(a.getAuthority());
			}
		}

		for (String w : want) {
			if (have.contains(w)) {
				return;
			}
		}

		log.debug("Access denied on {} for principal={} want={} have={}", jp.getSignature().toShortString(),
				auth.getName(), Arrays.toString(roles), have);
		throw new AccessDeniedException("Insufficient privileges");
	}

	private void denyUnauthenticated(JoinPoint jp) {
		log.debug("Unauthenticated access on {}", jp.getSignature().toShortString());
		throw new AuthenticationCredentialsNotFoundException("Authentication required");
	}

	private static String toAuthority(String role) {
		String r = role.trim();
		if (r.startsWith("ROLE_")) {
			return r;
		}
		return "ROLE_" + r;
	}
}

