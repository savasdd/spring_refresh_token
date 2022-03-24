package com.token.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.token.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByUsername(String username);

	Boolean existsByUsername(String username);

}
