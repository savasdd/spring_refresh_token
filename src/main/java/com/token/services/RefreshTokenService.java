package com.token.services;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.token.model.RefreshToken;
import com.token.repository.RefreshTokenRepository;
import com.token.repository.UserRepository;
import com.token.util.EnumUtil;
import com.token.util.exception.TokenRefreshException;

@Service
public class RefreshTokenService {

	@Autowired
	private RefreshTokenRepository refreshTokenRepository;

	@Autowired
	private UserRepository userRepository;

	public Optional<RefreshToken> findByToken(String token) {
		return refreshTokenRepository.findByToken(token);
	}

	public RefreshToken createRefreshToken(Long userId) {
		RefreshToken refreshToken = new RefreshToken();
		String sha3Hex = new DigestUtils("SHA-256")
				.digestAsHex(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));

		refreshToken.setKullaniciAdi(userRepository.findById(userId).get().getUsername());
		refreshToken.setExpiryDate(Instant.now().plusMillis(EnumUtil.TOKEN_REFRESH_EXPIRE_TIME));
		refreshToken.setToken(sha3Hex);

		refreshToken = refreshTokenRepository.save(refreshToken);
		return refreshToken;
	}

	public RefreshToken verifyExpiration(RefreshToken token) {
		if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
			refreshTokenRepository.delete(token);
			throw new TokenRefreshException(token.getToken(),
					"Refresh token was expired. Please make a new signin request");
		}

		return token;
	}

}