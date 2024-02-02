package org.rudi.microservice.strukture.service.organization;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.Test;
import org.rudi.common.core.DocumentContent;
import org.rudi.facet.acl.bean.User;
import org.rudi.facet.email.EMailService;
import org.rudi.facet.generator.exception.GenerationException;
import org.rudi.facet.generator.exception.GenerationModelNotFoundException;
import org.rudi.facet.generator.text.impl.TemplateGeneratorImpl;
import org.rudi.facet.kaccess.service.dataset.DatasetService;
import org.rudi.microservice.strukture.service.StruktureSpringBootTest;
import org.rudi.microservice.strukture.service.helper.UserOrganizationEmailHelper;
import org.rudi.microservice.strukture.storage.entity.organization.OrganizationEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;

@StruktureSpringBootTest
public class UserOrganizationEmailHelperTestUT {

	@Autowired
	private UserOrganizationEmailHelper userOrganizationEmailHelper;

	@MockBean
	private TemplateGeneratorImpl templateGenerator;

	@MockBean
	private EMailService eMailService;

	@MockBean
	DatasetService datasetService;

	@Value("${email.organization.password.update.mock}")
	private String organizationPasswordUpdateMock;

	@Test
	void sendUserOrganizationUpdatePasswordConfirmation_fails_on_bad_calling() {

		assertThrows(IllegalArgumentException.class,
				() -> userOrganizationEmailHelper.sendUserOrganizationUpdatePasswordConfirmation(null, null, null));

		assertThrows(IllegalArgumentException.class, () -> userOrganizationEmailHelper
				.sendUserOrganizationUpdatePasswordConfirmation(null, null, Locale.FRENCH));

		assertThrows(IllegalArgumentException.class, () -> userOrganizationEmailHelper
				.sendUserOrganizationUpdatePasswordConfirmation(new OrganizationEntity(), null, null));

		assertThrows(IllegalArgumentException.class, () -> userOrganizationEmailHelper
				.sendUserOrganizationUpdatePasswordConfirmation(new OrganizationEntity(), new ArrayList<>(), null));

		assertThrows(IllegalArgumentException.class, () -> userOrganizationEmailHelper
				.sendUserOrganizationUpdatePasswordConfirmation(null, new ArrayList<>(), null));

		assertThrows(IllegalArgumentException.class, () -> userOrganizationEmailHelper
				.sendUserOrganizationUpdatePasswordConfirmation(null, new ArrayList<>(), Locale.FRENCH));
	}

	@Test
	void sendUserOrganizationUpdatePasswordConfirmation_never_fails_when_good_calling() {
		assertDoesNotThrow(() -> userOrganizationEmailHelper
				.sendUserOrganizationUpdatePasswordConfirmation(new OrganizationEntity(), null, Locale.FRENCH));

		assertDoesNotThrow(() -> userOrganizationEmailHelper.sendUserOrganizationUpdatePasswordConfirmation(
				new OrganizationEntity(), new ArrayList<>(), Locale.FRENCH));
	}

	@Test
	void sendUserOrganizationUpdatePasswordConfirmation_never_fails_when_something_happened()
			throws GenerationException, IOException, GenerationModelNotFoundException {

		OrganizationEntity organization = new OrganizationEntity();
		organization.setName("R13n-4-F3r");
		organization.setDescription("Tu crois vraiment que je vais mettre quelque chose ?");
		organization.setOpeningDate(LocalDateTime.now());
		List<User> users = new ArrayList<>();
		User user = new User();
		user.setLogin("monmail@truc.com");
		user.setUuid(UUID.randomUUID());
		users.add(user);

		doThrow(new GenerationModelNotFoundException("kc")).when(templateGenerator).generateDocument(any());
		assertDoesNotThrow(() -> userOrganizationEmailHelper
				.sendUserOrganizationUpdatePasswordConfirmation(organization, users, Locale.FRENCH));

		doThrow(new GenerationException("kc")).when(templateGenerator).generateDocument(any());
		assertDoesNotThrow(() -> userOrganizationEmailHelper
				.sendUserOrganizationUpdatePasswordConfirmation(organization, users, Locale.FRENCH));

		doThrow(new IOException("kc")).when(templateGenerator).generateDocument(any());
		assertDoesNotThrow(() -> userOrganizationEmailHelper
				.sendUserOrganizationUpdatePasswordConfirmation(organization, users, Locale.FRENCH));
	}

	@Test
	void sendUserOrganizationUpdatePasswordConfirmation_tries_to_send_email()
			throws GenerationException, IOException, GenerationModelNotFoundException {

		OrganizationEntity organization = new OrganizationEntity();
		organization.setName("Poulpe-iscoming");
		organization.setDescription("Le méta TU j'adore ça");
		organization.setOpeningDate(LocalDateTime.now());
		List<User> users = new ArrayList<>();
		User user = new User();
		user.setLogin("ptdrtki@mail.com");
		user.setUuid(UUID.randomUUID());
		users.add(user);

		when(templateGenerator.generateDocument(any())).thenReturn(
				new DocumentContent(ContentType.TEXT_HTML.toString(), new File(organizationPasswordUpdateMock)));

		userOrganizationEmailHelper.sendUserOrganizationUpdatePasswordConfirmation(organization, users, Locale.FRENCH);

		verify(eMailService, times(1)).sendMailAndCatchException(any());
	}

}
