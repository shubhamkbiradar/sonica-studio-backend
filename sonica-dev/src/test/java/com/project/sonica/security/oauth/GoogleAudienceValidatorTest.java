package com.project.sonica.security.oauth;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

class GoogleAudienceValidatorTest {

	@Test
	void validate_success_whenAudienceContainsClientId() {
		GoogleAudienceValidator validator = new GoogleAudienceValidator("client-123");

		Jwt jwt = Jwt.withTokenValue("t")
				.header("alg", "none")
				.claim("aud", List.of("client-123", "other"))
				.issuedAt(Instant.now())
				.expiresAt(Instant.now().plusSeconds(60))
				.build();

		assertThat(validator.validate(jwt).hasErrors()).isFalse();
	}

	@Test
	void validate_failure_whenAudienceDoesNotContainClientId() {
		GoogleAudienceValidator validator = new GoogleAudienceValidator("client-123");

		Jwt jwt = Jwt.withTokenValue("t")
				.header("alg", "none")
				.claim("aud", List.of("other"))
				.issuedAt(Instant.now())
				.expiresAt(Instant.now().plusSeconds(60))
				.build();

		assertThat(validator.validate(jwt).hasErrors()).isTrue();
	}

	@Test
	void validate_failure_whenClientIdNotConfigured() {
		GoogleAudienceValidator validator = new GoogleAudienceValidator("   ");

		Jwt jwt = Jwt.withTokenValue("t")
				.header("alg", "none")
				.claim("aud", List.of("client-123"))
				.issuedAt(Instant.now())
				.expiresAt(Instant.now().plusSeconds(60))
				.build();

		assertThat(validator.validate(jwt).hasErrors()).isTrue();
	}
}

