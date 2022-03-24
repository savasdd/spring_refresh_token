package com.token.model;

import java.time.Instant;

import javax.persistence.Id;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
@Document(collection = "tokenrefresh")
public class RefreshToken {

	@Id
	private String id;

	@Indexed
	private String kullaniciAdi;

	private String token;

	private Instant expiryDate;

}
