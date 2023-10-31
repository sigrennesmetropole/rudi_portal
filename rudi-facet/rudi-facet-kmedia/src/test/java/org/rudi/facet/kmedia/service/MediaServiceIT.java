package org.rudi.facet.kmedia.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.rudi.common.core.DocumentContent;
import org.rudi.common.test.UUIDUtils;
import org.rudi.facet.dataverse.api.dataset.DatasetOperationAPI;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.kmedia.KmediaSpringBootIT;
import org.rudi.facet.kmedia.bean.KindOfData;
import org.rudi.facet.kmedia.bean.MediaDataset;
import org.rudi.facet.kmedia.bean.MediaOrigin;
import org.springframework.beans.factory.annotation.Autowired;

@KmediaSpringBootIT
class MediaServiceIT {

	@Autowired
	private MediaService mediaService;

	private static UUID providerId = null;
	private static UUID nonExistingProviderId = null;
	private static MediaDataset providerLogo = null;

	@BeforeAll
	static void initClassTest() {
		providerId = UUID.randomUUID();
		nonExistingProviderId = UUIDUtils.keepOnlyUUIDSegment(1, providerId); // on conserve uniquement un fragment (entre tirets) du providerId

		providerLogo = new MediaDataset();
		providerLogo.setAuthorAffiliation(MediaOrigin.PROVIDER);
		providerLogo.setAuthorIdentifier(providerId);
		providerLogo.setAuthorName("Rennes Metropole");
		providerLogo.setKindOfData(KindOfData.LOGO);
		providerLogo.setTitle("Logo de Rennes Metropole");
	}

	@AfterEach
	void tearDown() throws DataverseAPIException {
		mediaService.deleteMediaFor(MediaOrigin.PROVIDER, providerId, KindOfData.LOGO);
	}

	@AfterAll
	static void afterTestClass(@Autowired DatasetOperationAPI datasetOperationAPI) throws DataverseAPIException {
		if (!StringUtils.isEmpty(providerLogo.getDataverseDoi())) {
			datasetOperationAPI.deleteDataset(providerLogo.getDataverseDoi());
		}
	}

	@Test
	// RUDI-576 : La recherche de media doit être plus stricte et doit se faire sur le providerId complet
	void rechercheProviderNonExistant() throws DataverseAPIException {
		uploaderLogoParDefaut();

		final DocumentContent singleMediaFile = mediaService.getMediaFor(MediaOrigin.PROVIDER, nonExistingProviderId,
				KindOfData.LOGO);

		assertThat(singleMediaFile)
				.as("On ne doit pas trouver de résultat si un fragment d'UUID n'est pas celui attendu").isNull();
	}

	@Test
	void uploadEtDownload() throws DataverseAPIException {
		// ajout du logo au media créé
		final File file = uploaderLogoParDefaut();

		// téléchargement du logo du fournisseur
		final DocumentContent documentContent = telechargerLogo();

		assertThat(documentContent).isNotNull().as("Le document téléchargé a les propriétés attendues")
				.hasFieldOrPropertyWithValue("contentType", "image/png").hasFieldOrPropertyWithValue("fileSize", 33505L)
				.hasNoNullFieldsOrPropertiesExcept("url", "fileStream");
		assertThat(documentContent.getFileName()).as("L'extension du fichier n'a pas changé").endsWith(".png");
		assertThat(documentContent.getFile()).as("Le fichier téléchargé a le même contenu que celui téléversé")
				.hasSameBinaryContentAs(file);
	}

	@Nullable
	private DocumentContent telechargerLogo() throws DataverseAPIException {
		return mediaService.getMediaFor(MediaOrigin.PROVIDER, providerId, KindOfData.LOGO);
	}

	@Nonnull
	private File uploaderLogoParDefaut() throws DataverseAPIException {
		final File file = getLogo("LogoIRISA-web.png");
		mediaService.setMediaFor(MediaOrigin.PROVIDER, providerId, KindOfData.LOGO, file);
		return file;
	}

	@Nonnull
	private File getLogo(final String name) {
		return new File("src/test/resources/documentContent/" + name);
	}

	@Test
	void remplacerFichierMemeContenu() throws DataverseAPIException {
		// ajout du logo au media créé
		final File file = uploaderLogoParDefaut();

		// On essaie d'écraser le fichier par le même contenu
		mediaService.setMediaFor(MediaOrigin.PROVIDER, providerId, KindOfData.LOGO, file);

		// téléchargement du logo pour vérifier son contenu
		final DocumentContent documentContent = telechargerLogo();

		assertThat(documentContent).isNotNull().as("Le document téléchargé a les propriétés attendues")
				.hasFieldOrPropertyWithValue("contentType", "image/png").hasFieldOrPropertyWithValue("fileSize", 33505L)
				.hasNoNullFieldsOrPropertiesExcept("url", "fileStream");
		assertThat(documentContent.getFileName()).as("L'extension du fichier n'a pas changé").endsWith(".png");
		assertThat(documentContent.getFile()).as("Le fichier téléchargé a le même contenu que celui téléversé")
				.hasSameBinaryContentAs(file);
	}

	@Test
	void remplacerFichierContenuDifferent() throws DataverseAPIException {
		// ajout du logo V1 au media créé
		uploaderLogoParDefaut();

		// On essaie d'écraser le fichier par un contenu différent (logo V2)
		final File logoV2 = getLogo("LogoIRISA-bleu.png");
		mediaService.setMediaFor(MediaOrigin.PROVIDER, providerId, KindOfData.LOGO, logoV2);

		// téléchargement du logo pour vérifier son contenu
		final DocumentContent documentContent = telechargerLogo();

		assertThat(documentContent).isNotNull().as("Le document téléchargé a les propriétés attendues")
				.hasFieldOrPropertyWithValue("contentType", "image/png").hasFieldOrPropertyWithValue("fileSize", 2597L)
				.hasNoNullFieldsOrPropertiesExcept("url", "fileStream");
		assertThat(documentContent.getFileName()).as("L'extension du fichier n'a pas changé").endsWith(".png");
		assertThat(documentContent.getFile()).as("Le fichier téléchargé a le même contenu que celui téléversé")
				.hasSameBinaryContentAs(logoV2);
	}

}
