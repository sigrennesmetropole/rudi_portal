package org.rudi.facet.crypto;

import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.MGF1ParameterSpec;
import java.util.function.Supplier;

import javax.annotation.Nullable;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;

import lombok.Builder;
import lombok.Getter;

/**
 * Paramètres de l'algorithme de chiffrement hybride utilisé sur RUDI.
 *
 * <ul>
 * <li>keyPair : paire de clés asymétriques composée de la clé publique et de la clé privée</li>
 * <li>publicKey : clé publique utilisée pour chiffrer la secretKey et l'initializationVector du premier bloc</li>
 * <li>privateKey : clé privée associée à la clé publique (pour déchiffrer)</li>
 * <li>secretKey : clé symétrique utilisée pour chiffrer les données par bloc</li>
 * <li>firstBlock : premier bloc jouant le rôle d'en-têtes, avant les données chiffrées, contenant la secretKey et l'initializationVector chiffrés
 * avec la clé publique</li>
 * </ul>
 *
 * <p>
 * Liste des différents noms d'algorithmes supportés par Java : <a href=
 * "https://docs.oracle.com/javase/9/docs/specs/security/standard-names.html">https://docs.oracle.com/javase/9/docs/specs/security/standard-names.html</a>
 * </p>
 */
@Builder
public class RudiAlgorithmSpec implements AlgorithmParameterSpec {

	// Liste des algos dispos : https://docs.oracle.com/javase/8/docs/api/index.html?javax/crypto/Cipher.html

	/**
	 * Chiffrement de données suivant la suite de chiffrement <a href="https://ciphersuite.info/cs/TLS_AES_256_GCM_SHA384/">TLS_AES_256_GCM_SHA384</a>
	 */
	private static final Supplier<RudiAlgorithmSpecBuilder> TLS_AES_256_GCM_SHA384_TEMPLATE = () -> RudiAlgorithmSpec
			.builder()
			// On utilise exactement le même paramétrage que la librairie JavaScript "SubtleCrypto" :
			// https://developer.mozilla.org/en-US/docs/Web/API/SubtleCrypto/encrypt#aes-gcm
			.secretKeyAlgorithm("AES").secretKeySize(256).cipherAlgorithm("AES/GCM/NoPadding") // https://fr.wikipedia.org/wiki/Galois/Counter_Mode
	// TODO checksum SHA384
	;

	public static final RudiAlgorithmSpec DEFAULT = TLS_AES_256_GCM_SHA384_TEMPLATE
			.get().firstBlockSpec(FirstBlockSpec.builder()
					// On utilise exactement le même paramétrage que la librairie JavaScript "SubtleCrypto" :
					// https://developer.mozilla.org/en-US/docs/Web/API/SubtleCrypto/encrypt#rsa-oaep
					// Source : https://stackoverflow.com/a/59678340
					.keyPairAlgorithm("RSA") // cf rudi-tools/rudi-tools-wso2-handler/src/main/resources/encryption_key.key
					.keyPairKeySize(2048) // cf rudi-tools/rudi-tools-wso2-handler/src/main/resources/encryption_key.key
					.cipherAlgorithm("RSA/ECB/OAEPPadding")
					.cipherAlgorithmParams(new OAEPParameterSpec("SHA-256", "MGF1", new MGF1ParameterSpec("SHA-256"),
							PSource.PSpecified.DEFAULT))
					.build())
			.authenticationTagLength(128).initializationVectorLength(12).build();

	public static final RudiAlgorithmSpec AES_DEFAULT = RudiAlgorithmSpec.builder()
			// On utilise exactement le même paramétrage que la librairie JavaScript "SubtleCrypto" :
			// https://developer.mozilla.org/en-US/docs/Web/API/SubtleCrypto/encrypt#aes-gcm
			.secretKeyAlgorithm("AES").secretKeySize(256).cipherAlgorithm("AES/GCM/NoPadding") // https://fr.wikipedia.org/wiki/Galois/Counter_Mode
			.authenticationTagLength(128).initializationVectorLength(12).build();

	/**
	 * Paramètres de l'algorithme de chiffrement du premier bloc
	 */
	public final FirstBlockSpec firstBlockSpec;

	@Builder
	@Getter
	public static final class FirstBlockSpec {

		/**
		 * Algorithme ayant servi à générer la paire de clés (la clé publique et la clé privée) utilisée pour (dé)chiffrer le premier bloc
		 */
		final String keyPairAlgorithm;

		/**
		 * Taille utilisée pour générer la paire de clés (la clé publique et la clé privée) en bits
		 */
		final int keyPairKeySize;

		/**
		 * Algorithme utilisé pour chiffrer le premier bloc de données. La taille de ce bloc est égale à {@link #getFirstBlockSizeInBytes()}.
		 */
		final String cipherAlgorithm;

		/**
		 * Paramètres utilisés par {@link #cipherAlgorithm l'algorithme de chiffrement du premier bloc}. {@code null} si aucun paramètre nécessaire.
		 */
		@Nullable
		final AlgorithmParameterSpec cipherAlgorithmParams;

		/**
		 * Dans le cas d'une clé RSA, on peut calculer la taille des blocs chiffrés qu'elle produit. Cette taille est fixe quelque soit les données chiffrées.
		 */
		int getFirstBlockSizeInBytes() {
			return keyPairKeySize / 8;
		}

	}

	/**
	 * Clé secrète utilisé pour chiffrer tous les autres blocs après le premier. Elle, et le vecteur d'initialisation, sont chiffrés dans le premier bloc
	 * avec la clé publique associée à la clé privée.
	 */
	final String secretKeyAlgorithm;

	/**
	 * Taille de la clé privée en bits
	 */
	final int secretKeySize;

	/**
	 * the authentication tag length (in bits)
	 *
	 * @see GCMParameterSpec#getTLen()
	 */
	public final int authenticationTagLength;

	/**
	 * Initialization Vector length (in bytes)
	 *
	 * @see GCMParameterSpec#getIV()
	 */
	public final int initializationVectorLength;

	/**
	 * Algorithme utilisé pour chiffrer tous les blocs de données se trouvant après le premier bloc.
	 */
	public final String cipherAlgorithm;

	int getSecretKeySizeInBytes() {
		return secretKeySize / 8;
	}

	/**
	 * @return taille de bloc maximum que la clé privée peut chiffrer
	 */
	public int getMaximumBlockSizeInBytes() {
		return secretKeySize;
	}

}
