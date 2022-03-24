package com.token.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

import com.token.security.services.UserDetailsImpl;
import com.token.util.EnumUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class TokenManager {

	Key key1 = Keys.secretKeyFor(SignatureAlgorithm.HS256);

	private Key getSigningKey() {
		String sha3Hex = new DigestUtils("SHA-256").digestAsHex(EnumUtil.SECRET_KEY.getBytes(StandardCharsets.UTF_8));
		byte[] keyBytes = Decoders.BASE64.decode(sha3Hex);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	public String generateJwtToken(UserDetailsImpl userPrincipal) {
		return generateToken(userPrincipal.getUsername());
	}

	public String generateToken(String username) {
		return Jwts.builder().setSubject(username).setIssuer("enerjimpys")
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + EnumUtil.TOKEN_EXPIRE_TIME))
				.signWith(getSigningKey()).compact();
	}

	public boolean tokenValidate(String tokan) {
		if (getUserFromToken(tokan) != null && isExpired(tokan)) {
			return true;
		}
		return false;
	}

	public String getUserFromToken(String token) {
		Claims claims = getClaims(token); // key ile token çözüldü ve içindeki değerler parse edildi
		return claims.getSubject();// user
	}

	public Boolean isExpired(String token) {// geçerlilik
		Claims claims = getClaims(token);
		return claims.getExpiration().after(new Date(System.currentTimeMillis()));// bitiş zamanından sonra ise
	}

	private Claims getClaims(String token) {
		return Jwts.parser().setSigningKey(getSigningKey()).parseClaimsJws(token).getBody();
	}

}
