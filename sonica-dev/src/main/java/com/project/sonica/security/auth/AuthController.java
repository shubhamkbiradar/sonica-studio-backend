package com.project.sonica.security.auth;

import java.util.HashMap;
import java.util.Map;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.sonica.apiResponseWrapper.ApiResponse;
import com.project.sonica.exceptionHandler.TokenBlacklistedException;
import com.project.sonica.exceptionHandler.TokenExpiredException;
import com.project.sonica.security.JwtUtil;
import com.project.sonica.security.RegisterRequest;
import com.project.sonica.security.User;
import com.project.sonica.security.UserRepository;
import com.project.sonica.security.UserService;
import com.project.sonica.security.oauth.GoogleAuthRequest;
import com.project.sonica.security.oauth.GoogleOAuthService;
import com.project.sonica.security.token.BlacklistService;
import com.project.sonica.security.token.RefreshToken;
import com.project.sonica.security.token.RefreshTokenRepository;
import com.project.sonica.security.token.RefreshTokenService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private UserDetailsService userDetailsService;
	@Autowired
	private UserService userService;
	@Autowired
	private RefreshTokenService refreshTokenService;
	@Autowired
	private BlacklistService blacklistService;
	@Autowired
	private GoogleOAuthService googleOAuthService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private RefreshTokenRepository refreshTokenRepository;

	@PostMapping("/register")
	public ResponseEntity<ApiResponse<String>> register(@RequestBody RegisterRequest request) {
		User user = userService.registerUser(request);
		return ResponseEntity.ok(new ApiResponse<>(200, "User registered successfully", user.getUsername()));
	}

	@PostMapping("/login")
	public ResponseEntity<ApiResponse<Map<String, String>>> login(@RequestBody AuthRequest request) {
		// Use explicit credential verification so login behavior matches our persisted user model
		// (users are looked up by email in CustomUserDetailsService).
		String identifier = request.getUsername();
		User user = userRepository.findByEmail(identifier).or(() -> userRepository.findByUsername(identifier))
				.orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new BadCredentialsException("Invalid username or password");
		}

		// For JWT subject, prefer email (that's what CustomUserDetailsService uses).
		String principal = (user.getEmail() == null || user.getEmail().isBlank()) ? user.getUsername() : user.getEmail();
		UserDetails userDetails = userDetailsService.loadUserByUsername(principal);
		String jwt = jwtUtil.generateAccessToken(userDetails);
		RefreshToken refreshToken = refreshTokenService.createRefreshToken(principal);

		Map<String, String> tokens = new HashMap<>();
		tokens.put("accessToken", jwt);
		tokens.put("refreshToken", refreshToken.getToken());

		return ResponseEntity.ok(new ApiResponse<>(200, "Login successful", tokens));
	}

	/**
	 * Google OAuth2 (OpenID Connect) sign-in.
	 *
	 * Frontend flow:
	 * 1) Use Google Identity Services to obtain an ID token.
	 * 2) POST `{ "idToken": "..." }` to this endpoint.
	 * 3) Backend verifies the ID token and issues Sonica access/refresh tokens.
	 */
	@PostMapping("/google")
	public ResponseEntity<ApiResponse<Map<String, Object>>> google(@RequestBody GoogleAuthRequest request) {
		User user = googleOAuthService.upsertFromIdToken(request.getIdToken());

		UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
		String accessToken = jwtUtil.generateAccessToken(userDetails);
		RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());

		Map<String, Object> data = new HashMap<>();
		data.put("accessToken", accessToken);
		data.put("refreshToken", refreshToken.getToken());

		Map<String, String> userInfo = new HashMap<>();
		userInfo.put("email", user.getEmail());
		userInfo.put("firstName", user.getFirstName());
		userInfo.put("lastName", user.getLastName());
		userInfo.put("username", user.getUsername());
		userInfo.put("role", user.getRole() == null ? null : user.getRole().name());
		data.put("user", userInfo);

		return ResponseEntity.ok(new ApiResponse<>(200, "Google login successful", data));
	}

	/**
	 * Stateless JWT logout:
	 * - Revokes refresh token (DB delete + blacklist).
	 * - Optionally blacklists the provided access token until its expiry.
	 */
	@PostMapping("/logout")
	public ResponseEntity<ApiResponse<String>> logout(@RequestBody LogoutRequest request,
			jakarta.servlet.http.HttpServletRequest httpRequest) {
		String refreshToken = request == null ? null : request.getRefreshToken();

		if (refreshToken != null && !refreshToken.isBlank()) {
			refreshTokenRepository.findByToken(refreshToken).ifPresent(rt -> {
				blacklistService.blacklistToken(refreshToken, rt.getExpiryDate());
				refreshTokenRepository.delete(rt);
			});
		}

		String accessToken = request == null ? null : request.getAccessToken();
		if (accessToken == null || accessToken.isBlank()) {
			String authHeader = httpRequest.getHeader("Authorization");
			if (authHeader != null && authHeader.startsWith("Bearer ")) {
				accessToken = authHeader.substring(7);
			}
		}

		if (accessToken != null && !accessToken.isBlank()) {
			try {
				Instant exp = jwtUtil.extractExpiration(accessToken).toInstant();
				blacklistService.blacklistToken(accessToken, exp);
			} catch (RuntimeException e) {
				// Ignore parse errors: refresh token revocation is the primary logout mechanism.
				logger.warn("Failed to blacklist access token during logout: {}", e.getMessage());
			}
		}

		return ResponseEntity.ok(new ApiResponse<>(200, "Logout successful", null));
	}

	@PostMapping("/refresh")
	public ResponseEntity<ApiResponse<Map<String, String>>> refresh(@RequestBody Map<String, String> request) {
		String oldRefreshTokenStr = request.get("refreshToken");

		if (blacklistService.isBlacklisted(oldRefreshTokenStr)) {
			throw new TokenBlacklistedException("Refresh token is invalidated");
		}

		RefreshToken oldRefreshToken = refreshTokenService.findByToken(oldRefreshTokenStr)
				.map(refreshTokenService::verifyExpiration)
				.orElseThrow(() -> new TokenExpiredException("Refresh token expired"));

		UserDetails userDetails = userDetailsService.loadUserByUsername(oldRefreshToken.getUsername());
		String newAccessToken = jwtUtil.generateAccessToken(userDetails);

		RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(userDetails.getUsername());
		blacklistService.blacklistToken(oldRefreshTokenStr, oldRefreshToken.getExpiryDate());

		Map<String, String> tokens = new HashMap<>();
		tokens.put("accessToken", newAccessToken);
		tokens.put("refreshToken", newRefreshToken.getToken());

		return ResponseEntity.ok(new ApiResponse<>(200, "Token refreshed successfully", tokens));
	}

	@PostMapping("/forgot-password")
	public ResponseEntity<String> forgotPassword(@RequestParam String email, @RequestParam String newPassword) {
		try {
			userService.resetPassword(email, newPassword);
			return ResponseEntity.ok("Password reset successful!");
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
}
