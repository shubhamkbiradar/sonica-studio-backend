package com.project.sonica.security.oauth;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import com.project.sonica.aop.annotations.SonicaTx;
import com.project.sonica.security.AuthProvider;
import com.project.sonica.security.Role;
import com.project.sonica.security.User;
import com.project.sonica.security.UserRepository;

@Service
public class GoogleOAuthService {
	private final JwtDecoder googleJwtDecoder;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public GoogleOAuthService(@Qualifier("googleJwtDecoder") JwtDecoder googleJwtDecoder, UserRepository userRepository,
			PasswordEncoder passwordEncoder) {
		this.googleJwtDecoder = googleJwtDecoder;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@SonicaTx
	public User upsertFromIdToken(String idToken) {
		if (idToken == null || idToken.isBlank()) {
			throw new IllegalArgumentException("idToken is required");
		}

		// Validates signature, expiry, issuer, and audience via GoogleJwtDecoderConfig.
		Jwt jwt = googleJwtDecoder.decode(idToken);

		String email = jwt.getClaimAsString("email");
		Boolean emailVerified = jwt.getClaimAsBoolean("email_verified");
		String givenName = jwt.getClaimAsString("given_name");
		String familyName = jwt.getClaimAsString("family_name");
		String sub = jwt.getSubject(); // Google's stable user ID for this Google account.

		if (email == null || email.isBlank()) {
			throw new IllegalArgumentException("Google token missing email");
		}
		if (Boolean.FALSE.equals(emailVerified)) {
			throw new IllegalArgumentException("Google email is not verified");
		}

		User user = userRepository.findByEmail(email).orElseGet(User::new);
		boolean isNew = (user.getId() == null);

		user.setEmail(email);
		user.setProvider(AuthProvider.GOOGLE);
		user.setGoogleSub(sub);
		user.setFirstName(givenName);
		user.setLastName(familyName);

		// Keep username populated for existing APIs/UI that rely on it as a display handle.
		String displayName = buildDisplayName(givenName, familyName, email);
		user.setUsername(displayName);

		// Our security stack expects a non-null password even if the user signs in via Google.
		if (isNew || user.getPassword() == null || user.getPassword().isBlank()) {
			user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
		}

		if (user.getRole() == null) {
			user.setRole(Role.CUSTOMER);
		}

		return userRepository.save(user);
	}

	private static String buildDisplayName(String givenName, String familyName, String email) {
		String gn = givenName == null ? "" : givenName.trim();
		String fn = familyName == null ? "" : familyName.trim();
		String combined = (gn + " " + fn).trim();
		if (!combined.isBlank()) {
			return combined;
		}
		return email;
	}
}
