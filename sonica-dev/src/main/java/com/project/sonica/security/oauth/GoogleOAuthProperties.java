package com.project.sonica.security.oauth;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "sonica.oauth.google")
public class GoogleOAuthProperties {
	/**
	 * OAuth client ID created in Google Cloud Console.
	 * Used to validate the "aud" claim in Google ID tokens.
	 */
	private String clientId;

	/**
	 * Expected issuer for Google ID tokens (typically https://accounts.google.com).
	 */
	private String issuer;

	/**
	 * Google JWK Set URI used to validate ID token signatures.
	 */
	private String jwkSetUri;

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public String getJwkSetUri() {
		return jwkSetUri;
	}

	public void setJwkSetUri(String jwkSetUri) {
		this.jwkSetUri = jwkSetUri;
	}
}

