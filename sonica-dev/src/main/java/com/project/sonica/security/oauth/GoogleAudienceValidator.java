package com.project.sonica.security.oauth;

import java.util.List;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

final class GoogleAudienceValidator implements OAuth2TokenValidator<Jwt> {
	private static final OAuth2Error INVALID_AUDIENCE =
			new OAuth2Error("invalid_token", "Invalid Google ID token audience (aud).", null);

	private final String expectedClientId;

	GoogleAudienceValidator(String expectedClientId) {
		this.expectedClientId = expectedClientId;
	}

	@Override
	public OAuth2TokenValidatorResult validate(Jwt token) {
		if (expectedClientId == null || expectedClientId.isBlank()) {
			// Misconfiguration should fail fast; otherwise anyone's token could be accepted.
			return OAuth2TokenValidatorResult.failure(
					new OAuth2Error("server_error", "Google client-id is not configured.", null));
		}

		List<String> aud = token.getAudience();
		if (aud != null && aud.contains(expectedClientId)) {
			return OAuth2TokenValidatorResult.success();
		}
		return OAuth2TokenValidatorResult.failure(INVALID_AUDIENCE);
	}
}

