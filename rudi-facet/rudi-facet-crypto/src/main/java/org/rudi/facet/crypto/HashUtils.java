/**
 * RUDI Portail
 */
package org.rudi.facet.crypto;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author FNI18300
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HashUtils {

	private static final String SHA3_256 = "SHA3-256";

	private static ObjectMapper objectMapper = null;

	public static String sha3(@NotNull String input) throws NoSuchAlgorithmException {
		final MessageDigest digest = MessageDigest.getInstance(SHA3_256);
		final byte[] hashbytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
		return bytesToHex(hashbytes);
	}

	public static String saltSha3(@NotNull String input, @NotNull String salt) throws NoSuchAlgorithmException {
		return sha3(salt + input);
	}

	public static <T> String sha3(@NotNull T input) throws NoSuchAlgorithmException, JsonProcessingException {
		return sha3(getObjectMapper().writeValueAsString(input));
	}

	public static <T> String saltSha3(@NotNull T input, @NotNull String salt)
			throws NoSuchAlgorithmException, JsonProcessingException {
		return sha3(salt + getObjectMapper().writeValueAsString(input));
	}

	private static String bytesToHex(byte[] hash) {
		StringBuilder hexString = new StringBuilder(2 * hash.length);
		for (int i = 0; i < hash.length; i++) {
			String hex = Integer.toHexString(0xff & hash[i]);
			if (hex.length() == 1) {
				hexString.append('0');
			}
			hexString.append(hex);
		}
		return hexString.toString();
	}

	private static ObjectMapper getObjectMapper() {
		if (objectMapper == null) {
			objectMapper = new ObjectMapper();
			objectMapper.registerModule(new JavaTimeModule());
		}
		return objectMapper;
	}
}
