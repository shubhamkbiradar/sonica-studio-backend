package com.project.sonica.aop.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

/**
 * Conservative logging helpers.
 *
 * Default behavior avoids dumping arbitrary object graphs (which can contain PII/secrets,
 * or be expensive/recursive). If you need more details, explicitly log them at the call site.
 */
public final class SafeLog {
	private SafeLog() {
	}

	public static String value(Object v) {
		if (v == null) {
			return "null";
		}

		if (v instanceof CharSequence s) {
			return safeString(s.toString());
		}
		if (v instanceof Number || v instanceof Boolean || v instanceof UUID) {
			return String.valueOf(v);
		}
		if (v instanceof LocalDate || v instanceof LocalDateTime || v instanceof OffsetDateTime || v instanceof ZonedDateTime
				|| v instanceof Instant) {
			return String.valueOf(v);
		}
		if (v.getClass().isEnum()) {
			return v.getClass().getSimpleName() + "." + v;
		}
		if (v.getClass().isArray()) {
			return v.getClass().getComponentType().getSimpleName() + "[]";
		}
		if (v instanceof Collection<?> c) {
			return v.getClass().getSimpleName() + "(size=" + c.size() + ")";
		}
		if (v instanceof Map<?, ?> m) {
			return v.getClass().getSimpleName() + "(size=" + m.size() + ")";
		}

		// Avoid calling toString() on domain objects by default.
		return v.getClass().getSimpleName();
	}

	private static String safeString(String s) {
		String trimmed = s.trim();
		if (trimmed.isEmpty()) {
			return "\"\"";
		}

		// Very basic masking for obvious secret-like values.
		String lower = trimmed.toLowerCase();
		if (lower.startsWith("bearer ") || looksLikeJwt(trimmed) || looksLikeApiKey(trimmed)) {
			return "<redacted>";
		}

		if (trimmed.length() > 200) {
			return trimmed.substring(0, 200) + "...(len=" + trimmed.length() + ")";
		}
		return trimmed;
	}

	private static boolean looksLikeJwt(String s) {
		// "header.payload.signature" (not rigorous, just a guardrail)
		int first = s.indexOf('.');
		if (first <= 0) {
			return false;
		}
		int second = s.indexOf('.', first + 1);
		return second > first + 1 && second < s.length() - 1;
	}

	private static boolean looksLikeApiKey(String s) {
		// Heuristic: long-ish single token without spaces.
		return s.length() >= 32 && s.indexOf(' ') < 0;
	}
}

