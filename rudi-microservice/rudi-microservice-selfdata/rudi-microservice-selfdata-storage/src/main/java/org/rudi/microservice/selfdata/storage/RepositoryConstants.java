package org.rudi.microservice.selfdata.storage;

public class RepositoryConstants {
	public static final String DATASET_UUID_FIELD = "datasetUuid";

	// Actuellement on ne gère pas plusieurs types de demande mais on peut trier par ça
	public static final String PROCESS_DEFINITION_KEY_FIELD = "processDefinitionKey";

	public static final String UPDATED_DATE_FIELD = "updatedDate";
	public static final String CREATION_DATE_FIELD = "creationDate";
	public static final String FUNCTIONAL_STATUS_FIELD = "functionalStatus";
	public static final String INITATOR_FIELD = "initiator";

	private RepositoryConstants() {
	}
}
