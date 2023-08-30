package org.rudi.microservice.acl.core.bean;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author FNI18300
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class UserSearchCriteria {

	private String login;

	/**
	 * Mot-de-passe en clair.
	 *
	 * <p>
	 * <b>Attention</b> : l'encodage d'un même mot-de-passe ne produira pas toujours la même valeur.
	 * C'est pourquoi on ne peut pas utiliser ce critère directement dans une requête SQL.
	 * </p>
	 *
	 * <p>Cf implémentation de la méthode org.rudi.microservice.acl.service.helper.PasswordHelper#matches(CharSequence, String)</p>
	 */
	private String password;

	private String lastname;

	private String firstname;

	private String company;

	private UserType type;

	private List<UUID> roleUuids;

	private List<UUID> userUuids;

	private String userEmail;

	private String loginAndDenomination;
}
