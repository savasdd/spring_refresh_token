package com.token.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.token.model.RefreshToken;

public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {

	//@Query("{ 'token' : ?0 }")
	Optional<RefreshToken> findByToken(String token);

}
