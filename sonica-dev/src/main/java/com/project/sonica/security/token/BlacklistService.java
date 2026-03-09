package com.project.sonica.security.token;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class BlacklistService {
    @Autowired
    private BlacklistedTokenRepository blacklistedTokenRepository;

    public void blacklistToken(String token, Instant expiryDate) {
        BlacklistedToken blacklistedToken = new BlacklistedToken();
        blacklistedToken.setToken(token);
        blacklistedToken.setExpiryDate(expiryDate);
        blacklistedTokenRepository.save(blacklistedToken);
    }

    public boolean isBlacklisted(String token) {
        return blacklistedTokenRepository.existsByToken(token);
    }

    // Optional: cleanup expired blacklisted tokens
    @Scheduled(cron = "0 0 * * * *") // hourly
    public void cleanupExpiredTokens() {
        Instant now = Instant.now();
        blacklistedTokenRepository.findAll().stream()
            .filter(t -> t.getExpiryDate().isBefore(now))
            .forEach(blacklistedTokenRepository::delete);
    }
}
