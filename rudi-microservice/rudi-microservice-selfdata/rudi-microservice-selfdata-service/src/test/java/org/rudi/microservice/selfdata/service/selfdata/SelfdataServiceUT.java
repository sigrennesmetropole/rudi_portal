package org.rudi.microservice.selfdata.service.selfdata;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.rudi.bpmn.core.bean.Status;
import org.rudi.facet.bpmn.exception.InvalidDataException;
import org.rudi.facet.bpmn.helper.form.FormHelper;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.kaccess.bean.DictionaryEntry;
import org.rudi.facet.kaccess.bean.Language;
import org.rudi.facet.kaccess.bean.MatchingData;
import org.rudi.facet.kaccess.bean.MatchingData.TypeEnum;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.bean.MetadataExtMetadata;
import org.rudi.facet.kaccess.bean.MetadataExtMetadataExtSelfdata;
import org.rudi.facet.kaccess.bean.SelfdataContent;
import org.rudi.facet.kaccess.service.dataset.DatasetService;
import org.rudi.microservice.selfdata.service.SelfdataSpringBootTest;
import org.rudi.microservice.selfdata.service.helper.selfdatamatchingdata.MatchingDataCipherOperator;
import org.rudi.microservice.selfdata.service.helper.selfdatamatchingdata.SelfdataMatchingDataHelper;
import org.rudi.microservice.selfdata.service.selfdata.impl.SelfdataServiceImpl;
import org.rudi.microservice.selfdata.storage.dao.selfdatainformationrequest.SelfdataInformationRequestDao;
import org.rudi.microservice.selfdata.storage.entity.selfdatainformationrequest.SelfdataInformationRequestEntity;
import org.rudi.microservice.selfdata.storage.entity.selfdatainformationrequest.SelfdataInformationRequestStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import lombok.RequiredArgsConstructor;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SelfdataSpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class SelfdataServiceUT {

	private final SelfdataServiceImpl selfdataService;
	private final FormHelper formHelper;
	private final SelfdataInformationRequestDao selfdataInformationRequestDao;
	private final MatchingDataCipherOperator matchingDataCipherOperator;

	@MockBean
	private DatasetService datasetService;

	@Test
	void test_migrate_selfdata_matchingdata()
			throws InvalidDataException, DataverseAPIException, GeneralSecurityException, IOException {
		UUID uuid = UUID.fromString("20ca8dc6-dd92-45c7-bb41-4ffe51b78c14");
		Map<String, Object> datas = new HashMap<>();
		datas.put("code1", null);
		datas.put("code2", "test");
		datas.put("code3", "test2");
		datas.put("code4", SelfdataMatchingDataHelper.SELFDATA_CRYPTED_VALUE_IDENTIFIER + matchingDataCipherOperator.encrypt("test", "selfdata-matchingdata-key-20220101"));
		datas.put("code5", SelfdataMatchingDataHelper.SELFDATA_CRYPTED_VALUE_IDENTIFIER + matchingDataCipherOperator.encrypt("test"));
		for (int i = 0; i < 15; i++) {
			createSelfdataInformationRequest("Description" + i, uuid, datas);
		}
		MetadataExtMetadata extMetadata = new MetadataExtMetadata();
		MetadataExtMetadataExtSelfdata extSelfdata = new MetadataExtMetadataExtSelfdata();
		SelfdataContent selfdataContent = new SelfdataContent();
		selfdataContent.addMatchingDataItem(createMatchingData("code1"));
		selfdataContent.addMatchingDataItem(createMatchingData("code2"));
		selfdataContent.addMatchingDataItem(createMatchingData("code4"));
		selfdataContent.addMatchingDataItem(createMatchingData("code5"));
		extSelfdata.setExtSelfdataContent(selfdataContent);
		extMetadata.setExtSelfdata(extSelfdata);
		Metadata metadata = new Metadata();
		metadata.setGlobalId(uuid);
		metadata.setExtMetadata(extMetadata);
		when(datasetService.getDataset(any(UUID.class))).thenReturn(metadata);
		try {
			selfdataService.recryptSelfdataInformationRequest("selfdata-matchingdata-key-20220101");
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	private MatchingData createMatchingData(String code) {
		MatchingData matchingData = new MatchingData();
		matchingData.setCode(code);
		matchingData.setType(TypeEnum.STRING);
		matchingData.setLabel(new ArrayList<>());
		matchingData.getLabel().add(createDictionaryEntry(code));
		matchingData.setHelp(new ArrayList<>());
		matchingData.getHelp().add(createDictionaryEntry(code));
		return matchingData;
	}

	DictionaryEntry createDictionaryEntry(String label) {
		DictionaryEntry a = new DictionaryEntry();
		a.setLang(Language.FR_FR);
		a.setText(label);
		return a;
	}

	protected void createSelfdataInformationRequest(String description, UUID datasetuuid, Map<String, Object> datas)
			throws InvalidDataException {
		SelfdataInformationRequestEntity item = new SelfdataInformationRequestEntity();
		item.setUuid(UUID.randomUUID());
		item.setCreationDate(LocalDateTime.now());
		item.setData(formHelper.deshydrateData(datas));
		item.setDescription(description);
		item.setFunctionalStatus("fs");
		item.setInitiator("initiator");
		item.setProcessDefinitionKey("processkey");
		item.setProcessDefinitionVersion(1);
		item.setSelfdataInformationRequestStatus(SelfdataInformationRequestStatus.IN_PROGRESS);
		item.setStatus(Status.PENDING);
		item.setUpdatedDate(LocalDateTime.now());
		item.setUpdator("updator");
		item.setDatasetUuid(datasetuuid);
		selfdataInformationRequestDao.save(item);
	}

}
