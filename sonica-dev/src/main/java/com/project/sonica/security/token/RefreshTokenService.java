package com.project.sonica.security.token;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import com.project.sonica.apiResponseWrapper.ApiResponse;
import com.project.sonica.exceptionHandler.TokenBlacklistedException;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//hybrid refresh approach rotation

@Service
public class RefreshTokenService {
	@Autowired
	private RefreshTokenRepository refreshTokenRepository;
	@Autowired
	private BlacklistService blacklistService;

	public RefreshToken createRefreshToken(String username) {
		// delete old refresh tokens for this user
		refreshTokenRepository.deleteByUsername(username);

		RefreshToken refreshToken = new RefreshToken();
		refreshToken.setUsername(username);
		refreshToken.setToken(UUID.randomUUID().toString());
		refreshToken.setExpiryDate(Instant.now().plus(30, ChronoUnit.DAYS));
		return refreshTokenRepository.save(refreshToken);
	}

	public Optional<RefreshToken> findByToken(String token) {
		return refreshTokenRepository.findByToken(token);
	}

	public RefreshToken verifyExpiration(RefreshToken token) {
		if (token.getExpiryDate().isBefore(Instant.now())) {
			refreshTokenRepository.delete(token);
			throw new RuntimeException("Refresh token expired. Please login again.");
		}
		return token;
	}

	private static final Logger logger = LoggerFactory.getLogger(RefreshToken.class);

	@PostMapping("/refresh")
	public ResponseEntity<ApiResponse<Map<String, String>>> refresh(@RequestBody Map<String, String> request,
			HttpServletRequest httpRequest) {
		String oldRefreshTokenStr = request.get("refreshToken");
		String ip = httpRequest.getRemoteAddr();

		if (blacklistService.isBlacklisted(oldRefreshTokenStr)) {
			logger.error("Blacklisted refresh token used from IP={} at {}", ip, Instant.now());
			throw new TokenBlacklistedException("Refresh token is invalidated");
		}
		return refresh(request, httpRequest);
	}
}

//Benefits of Hybrid Approach
//- Revocable refresh tokens → stored in DB, can be deleted on logout or forced invalidation.
//- Stateless access tokens → short-lived JWTs for API calls.
//- Security → compromised refresh tokens can be invalidated immediately.
//- Flexibility → supports both JWT validation and DB-backed token management.

//@Service
//public class RefreshTokenService {
//	@Autowired
//	private RefreshTokenRepository refreshTokenRepository;
//
//	public Optional<RefreshToken> findByToken(String token) {
//		return refreshTokenRepository.findByToken(token);
//	}
//
//	public RefreshToken createRefreshToken(String username) {
//		RefreshToken refreshToken = new RefreshToken();
//		refreshToken.setUsername(username);
//		refreshToken.setToken(UUID.randomUUID().toString());
//		refreshToken.setExpiryDate(Instant.now().plus(30, ChronoUnit.DAYS));
//		return refreshTokenRepository.save(refreshToken);
//	}
//
//	public RefreshToken verifyExpiration(RefreshToken token) {
//		if (token.getExpiryDate().isBefore(Instant.now())) {
//			refreshTokenRepository.delete(token);
//			throw new RuntimeException("Refresh token expired. Please login again.");
//		}
//		return token;
//	}
//
//	public void deleteByUsername(String username) {
//		refreshTokenRepository.deleteByUsername(username);
//	}
//
//}
