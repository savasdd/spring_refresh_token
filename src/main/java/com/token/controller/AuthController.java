package com.token.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.token.model.RefreshToken;
import com.token.model.User;
import com.token.repository.UserRepository;
import com.token.security.TokenManager;
import com.token.security.request.LoginRequest;
import com.token.security.request.RefreshTokenRequest;
import com.token.security.request.TokenRequest;
import com.token.security.response.MessageResponse;
import com.token.security.response.TokenRefreshResponse;
import com.token.security.response.TokenResponse;
import com.token.security.services.UserDetailsImpl;
import com.token.services.RefreshTokenService;
import com.token.util.exception.TokenRefreshException;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AuthController {

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	TokenManager jwtUtils;

	@Autowired
	RefreshTokenService refreshTokenService;

	@PostMapping(value = "/auth/getToken")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest request) {

		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

		String jwt = jwtUtils.generateJwtToken(userDetails);

		RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
		TokenResponse response = new TokenResponse(jwt, refreshToken.getToken(), userDetails.getUsername());

		System.err.println("Generate Token For: " + request.getUsername());
		return ResponseEntity.ok(response);
	}

	@PostMapping("/auth/refreshToken")
	public ResponseEntity<?> refreshtoken(@Valid @RequestBody RefreshTokenRequest request) {
		String requestRefreshToken = request.getRefreshToken();

		return refreshTokenService.findByToken(requestRefreshToken).map(refreshTokenService::verifyExpiration)
				.map(user -> {
					String token = jwtUtils.generateToken(user.getKullaniciAdi());
					return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
				})
				.orElseThrow(() -> new TokenRefreshException(requestRefreshToken, "Refresh token is not in database!"));
	}

	@PostMapping("/auth/register")
	public ResponseEntity<?> registerUser(@Valid @RequestBody TokenRequest signUpRequest) {
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
		}

		User user = new User();
		user.setUsername(signUpRequest.getUsername());
		user.setPassword(encoder.encode(signUpRequest.getPassword()));
		user.setVersion(0L);
		userRepository.save(user);

		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	}

}
