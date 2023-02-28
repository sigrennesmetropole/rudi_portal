package org.rudi.facet.dataverse.constant;

import java.util.List;

import org.rudi.facet.dataverse.fields.DatasetMetadataBlockElementSpec;
import org.rudi.facet.dataverse.fields.FieldSpec;
import org.rudi.facet.dataverse.fields.FieldSpecNamingCase;
import org.rudi.facet.dataverse.fields.RootFieldSpec;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CitationMetadataField {

	/**
	 * Regroupe tous les champs Citation.
	 * Se traduit par un {@link org.rudi.facet.dataverse.bean.DatasetMetadataBlockElement} côté Dataverse.
	 */
	public static final FieldSpec ROOT = new RootFieldSpec(Dataset.class, "", FieldSpecNamingCase.CAMEL_CASE);

	public static final FieldSpec TITLE = ROOT.newChildFromJavaField("title");
	public static final FieldSpec SUBJECT = ROOT.newChildFromJavaField("subject");
	public static final FieldSpec AUTHOR = ROOT.newChildFromJavaField("author");
	public static final FieldSpec AUTHOR_NAME = AUTHOR.newChildFromJavaField(Author.class, "name");
	public static final FieldSpec AUTHOR_IDENTIFIER = AUTHOR.newChildFromJavaField(Author.class, "identifier");
	public static final FieldSpec AUTHOR_AFFILIATION = AUTHOR.newChildFromJavaField(Author.class, "affiliation");
	public static final FieldSpec CONTACT = ROOT.newChildFromJavaField("datasetContact");
	public static final FieldSpec CONTACT_NAME = CONTACT.newChildFromJavaField(DatasetContact.class, "name");
	public static final FieldSpec CONTACT_AFFILIATION = CONTACT.newChildFromJavaField(DatasetContact.class, "affiliation");
	public static final FieldSpec CONTACT_EMAIL = CONTACT.newChildFromJavaField(DatasetContact.class, "email");
	public static final FieldSpec DESCRIPTION = ROOT.newChildFromJavaField("dsDescription");
	public static final FieldSpec DESCRIPTION_VALUE = DESCRIPTION.newChildFromJavaField(DsDescription.class, "value");
	public static final FieldSpec DESCRIPTION_DATE = DESCRIPTION.newChildFromJavaField(DsDescription.class, "date");

	public static final DatasetMetadataBlockElementSpec CITATION_ELEMENT_SPEC = new DatasetMetadataBlockElementSpec(ROOT)
			.add(TITLE)
			.add(AUTHOR,
					AUTHOR_NAME,
					AUTHOR_AFFILIATION)
			.add(CONTACT,
					CONTACT_EMAIL,
					CONTACT_AFFILIATION,
					CONTACT_NAME)
			.add(DESCRIPTION,
					DESCRIPTION_DATE,
					DESCRIPTION_VALUE)
			.add(SUBJECT);


	@SuppressWarnings({ "unused", "squid:S1068" }) // Classe utilisée pour l'introspection
	private static class Dataset {
		private String title;
		private List<Author> author;
		private List<DatasetContact> datasetContact;
		private List<Subject> subject;
		private List<DsDescription> dsDescription;
		private List<String> kindOfData;
	}

	@SuppressWarnings({ "unused", "squid:S1068" }) // Classe utilisée pour l'introspection
	private static class Author {
		private String name;
		private String identifier;
		private String affiliation;
	}

	@SuppressWarnings({ "unused", "squid:S1068" }) // Classe utilisée pour l'introspection
	private static class DatasetContact {
		private String email;
		private String affiliation;
		private String name;
	}

	@SuppressWarnings({ "unused", "squid:S1068" }) // Classe utilisée pour l'introspection
	@RequiredArgsConstructor
	private enum Subject {
		OTHER("Other");

		private final String value;
	}

	@SuppressWarnings({ "unused", "squid:S1068" }) // Classe utilisée pour l'introspection
	private static class DsDescription {
		private String date;
		private String value;
	}

}
