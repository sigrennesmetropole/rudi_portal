package org.rudi.microservice.selfdata.service.selfdata;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import org.rudi.microservice.selfdata.service.SelfdataSpringBootTest;
import org.rudi.microservice.selfdata.service.helper.selfdatamatchingdata.MatchingDataCipherOperator;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SelfdataSpringBootTest
class MatchingDataCipherOperatorUT {

	@Autowired
	private MatchingDataCipherOperator matchingDataCipherOperator;

	/**
	 * Test de crypter/décrypter une chaine de caractère
	 *
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	@Test
	void test_encrypt_and_decript_string() throws GeneralSecurityException, IOException {
		String test = "Hello there !";

		String encryptedTest = matchingDataCipherOperator.encrypt(test);
		assertNotEquals(test, encryptedTest, "Après cryptage les chaines ne sont plus sensées être identiques.");

		String decryptedTest = matchingDataCipherOperator.decrypt(encryptedTest);
		assertNotEquals(encryptedTest, decryptedTest,
				"La chaîne est sensée être décryptée, donc différente de la chaîne cryptée.");
		assertEquals(test, decryptedTest, "La chaine est sensée être décryptée, donc égale à la chaine d'origine.");

	}

	/**
	 * Test la taille limite chiffrable et déchiffrable : actuellement 245 caractères UTF8
	 */
	@Test
	void test_limit_size_encryptible_decryptible() {
		if (matchingDataCipherOperator != null) {
			int i = 0;
			StringBuilder test = new StringBuilder();
			Random rand = new Random();

			// 2048 étant la taille maximale chiffrable en local.
			// Cette limite est laissée en cas de régression, pour déterminer une borne minimale
			while (i < 2048) {
				i++;
				test.append(rand.nextInt(10));

				final int index = i;
				final String uncryptedYet = test.toString();
				final AtomicReference<String> crypted = new AtomicReference<>();
				final AtomicReference<String> decrypted = new AtomicReference<>();
				assertDoesNotThrow(() -> {
					crypted.set(matchingDataCipherOperator.encrypt(uncryptedYet));
				}, String.format("La chaîne : \" %s\" ne peut pas être cryptée, elle est de longeur : %d.", uncryptedYet,
						index));
				assertDoesNotThrow(() -> {
					decrypted.set(matchingDataCipherOperator.decrypt(String.valueOf(crypted)));
				}, String.format("La chaîne : \" %s\" ne peut pas être décryptée, elle est de longeur : %d.", uncryptedYet,
						index));
			}

		}
	}
}
