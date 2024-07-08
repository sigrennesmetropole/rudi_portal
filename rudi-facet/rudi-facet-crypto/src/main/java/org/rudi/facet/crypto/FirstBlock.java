package org.rudi.facet.crypto;

import javax.crypto.SecretKey;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class FirstBlock {
	private final SecretKey secretKey;
	private final byte[] initialisationVector;
}
