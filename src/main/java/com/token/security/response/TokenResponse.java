package com.token.security.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponse {

	private String token;
	private String type = "Bearer";
	private String refreshToken;
	private String username;

	public TokenResponse(String accessToken, String refreshToken, String username) {
		this.token = accessToken;
		this.refreshToken = refreshToken;
		this.username = username;
	}
}
