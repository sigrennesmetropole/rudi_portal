package org.rudi.facet.cms.impl;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rudi.facet.cms.CmsSpringBootTest;
import org.rudi.facet.cms.impl.configuration.CmsMagnoliaConfiguration;
import org.rudi.facet.cms.impl.mapper.CmsCategoryMapperImpl;
import org.rudi.facet.cms.impl.utils.ResourceUriRewriter;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
@CmsSpringBootTest
class MagnoliaServiceImplUT {

	private static final String FILE_WITH_TAG_FIRST = "inputFiles/selfTagFirstPosition.html";
	private static final String FILE_WITH_TAG_LAST = "inputFiles/selfTagLastPosition.html";
	private static final String FILE_WITH_RESOURCES_IMAGE_BACKGROUND = "inputFiles/resourcesImagesInBackGround.html";
	private static final String FILE_WITH_RESOURCES_IMAGE_IMG = "inputFiles/resourcesImagesInImageBalise.html";
	private static final String FILE_WITH_RESOURCES_FILE_AHREF = "inputFiles/resourcesFileInLink.html";
	private static final String FILE_WITH_ALL_SELF_AND_RESOURCES = "inputFiles/completeExemple.html";

	private MagnoliaServiceImpl magnoliaService;

	@Mock
	private CmsMagnoliaTermsHandler termsHandler;

	@Mock
	private CmsMagnoliaConfiguration cmsMagnoliaConfiguration;

	@Autowired
	private List<ResourceUriRewriter> rewriters;

	@InjectMocks
	private CmsCategoryMapperImpl cmsCategoryMapper;

	@BeforeEach
	void setUp() {
		magnoliaService = new MagnoliaServiceImpl(cmsMagnoliaConfiguration, Arrays.asList(termsHandler),
				cmsCategoryMapper, rewriters);

		// lenient() evite le lancement d'une erreur si le mock n'est pas appelé par une fonction.
		lenient().when(cmsMagnoliaConfiguration.getFrontOfficeRoute()).thenReturn("/default/value/self");
		lenient().when(cmsMagnoliaConfiguration.getFrontOfficeResourcesRoute()).thenReturn("/default/value/resources");
	}

	private Element getElement(String fileToParse, String baseUri, String cssQuery)
			throws IOException, URISyntaxException {
		Path path = Paths.get(getClass().getClassLoader().getResource(fileToParse).toURI());

		Stream<String> lines = Files.lines(path);
		String data = lines.collect(Collectors.joining("\n"));
		lines.close();

		Document document = Jsoup.parse(data, baseUri);

		return document.selectFirst(cssQuery);
	}

	@Test
	void replaceSelfLinksFirstPosition() throws IOException, URISyntaxException {
		Element element = getElement(MagnoliaServiceImplUT.FILE_WITH_TAG_FIRST, "UTF-8", "div." + "terms-container");
		Element elementUnchanged = element.clone();

		assertThat(element).as("Le fichier contient bel et bien au moins un self")
				.matches(e -> !e.select(MagnoliaServiceImpl.SELF_CSS_QUERY).isEmpty());

		Element modifiedElement = magnoliaService.replaceSelfLinks(element);

		assertThat(modifiedElement).as("Le résultat doit être différent de l'input").isNotEqualTo(elementUnchanged)
				.as("Le résultat ne doit plus contenir de @self")
				.matches(e -> e.select(MagnoliaServiceImpl.SELF_CSS_QUERY).isEmpty());
	}

	@Test
	void replaceSelfLinksLastPosition() throws IOException, URISyntaxException {
		Element element = getElement(MagnoliaServiceImplUT.FILE_WITH_TAG_LAST, "UTF-8", "div." + "terms-container");
		Element elementUnchanged = element.clone();

		assertThat(element).as("Le fichier contient bel et bien au moins un self")
				.matches(e -> !e.select(MagnoliaServiceImpl.SELF_CSS_QUERY).isEmpty());

		Element modifiedElement = magnoliaService.replaceSelfLinks(element);

		assertThat(modifiedElement).as("Le résultat doit être différent de l'input").isNotEqualTo(elementUnchanged)
				.as("Le résultat ne doit plus contenir de @self")
				.matches(e -> e.select(MagnoliaServiceImpl.SELF_CSS_QUERY).isEmpty());
	}

	@Test
	void replaceImageLinkInBackground() throws IOException, URISyntaxException {
		Element element = getElement(MagnoliaServiceImplUT.FILE_WITH_RESOURCES_IMAGE_BACKGROUND, "UTF-8",
				"div." + "terms-container");
		Element elementUnchanged = element.clone();

		assertThat(element).as("Le fichier contient bel et bien une image en background")
				.matches(e -> !e.select(MagnoliaServiceImpl.STYLE_CSS_QUERY).isEmpty());

		Element modifiedElement = magnoliaService.replaceResourcesLinks(element);

		assertThat(modifiedElement).as("Le resultat doit être different de l'input").isNotEqualTo(elementUnchanged)
				.as("Le résultat ne doit plus contenir le même lien dans le background")
				.matches(e -> e.select(MagnoliaServiceImpl.STYLE_CSS_QUERY).isEmpty());
	}

	@Test
	void replaceImageLinkInImg() throws IOException, URISyntaxException {
		Element element = getElement(MagnoliaServiceImplUT.FILE_WITH_RESOURCES_IMAGE_IMG, "UTF-8",
				"div." + "terms-container");
		Element elementUnchanged = element.clone();

		assertThat(element).as("Le fichier contient bel et bien une image dans une balise img")
				.matches(e -> !e.select(MagnoliaServiceImpl.IMAGE1_CSS_QUERY).isEmpty());

		Element modifiedElement = magnoliaService.replaceResourcesLinks(element);

		assertThat(modifiedElement).as("Le resultat doit être different de l'input").isNotEqualTo(elementUnchanged)
				.as("Le résultat ne doit plus contenir le même lien dans la balise")
				.matches(e -> e.select(MagnoliaServiceImpl.IMAGE1_CSS_QUERY).isEmpty());
	}

	@Test
	void replaceResourceHref() throws IOException, URISyntaxException {
		Element element = getElement(MagnoliaServiceImplUT.FILE_WITH_RESOURCES_FILE_AHREF, "UTF-8",
				"div." + "terms-container");
		Element elementUnchanged = element.clone();

		assertThat(element).as("Le fichier contient bel et bien une ressource dans un href")
				.matches(e -> !e.select(MagnoliaServiceImpl.ANCHOR_CSS_QUERY).isEmpty());

		Element modifiedElement = magnoliaService.replaceResourcesLinks(element);

		assertThat(modifiedElement).as("Le resultat doit être different de l'input").isNotEqualTo(elementUnchanged)
				.as("Le résultat ne doit plus contenir le même lien dans le href de la balise a")
				.matches(e -> e.select(MagnoliaServiceImpl.ANCHOR_CSS_QUERY).isEmpty());
	}

	@Test
	void replaceSelfImageAndResource() throws IOException, URISyntaxException {
		Element element = getElement(MagnoliaServiceImplUT.FILE_WITH_ALL_SELF_AND_RESOURCES, "UTF-8",
				"div." + "news-container");
		Element elementUnchanged = element.clone();

		assertThat(element).as("Le fichier contient bel et bien au moins un self")
				.matches(e -> !e.select(MagnoliaServiceImpl.SELF_CSS_QUERY).isEmpty())
				.as("Le fichier contient bel et bien une image en background")
				.matches(e -> !e.select(MagnoliaServiceImpl.STYLE_CSS_QUERY).isEmpty())
				.as("Le fichier contient bel et bien une image dans une balise img")
				.matches(e -> !e.select(MagnoliaServiceImpl.IMAGE1_CSS_QUERY).isEmpty())
				.as("Le fichier contient bel et bien une ressource dans un href")
				.matches(e -> !e.select(MagnoliaServiceImpl.ANCHOR_CSS_QUERY).isEmpty())
				.as("Le fichier contient bel et bien un link")
				.matches(e -> !e.select(MagnoliaServiceImpl.LINK_CSS_QUERY).isEmpty());

		magnoliaService.replaceSelfLinks(element);
		magnoliaService.replaceResourcesLinks(element);

		assertThat(element).as("Le résultat doit être différent de l'input").isNotEqualTo(elementUnchanged)
				.as("Le résultat ne doit plus contenir de @self")
				.matches(e -> e.select(MagnoliaServiceImpl.SELF_CSS_QUERY).isEmpty())
				.as("Le résultat ne doit plus contenir le même lien dans le background")
				.matches(e -> e.select(MagnoliaServiceImpl.STYLE_CSS_QUERY).isEmpty())
				.as("Le résultat ne doit plus contenir le même lien dans la balise")
				.matches(e -> e.select(MagnoliaServiceImpl.IMAGE1_CSS_QUERY).isEmpty())
				.as("Le résultat ne doit plus contenir le même lien dans le href de la balise a")
				.matches(e -> e.select(MagnoliaServiceImpl.ANCHOR_CSS_QUERY).isEmpty())
				.as("Le résultat ne doit plus contenir le même link")
				.matches(e -> e.select(MagnoliaServiceImpl.LINK_CSS_QUERY).isEmpty());
	}

}
