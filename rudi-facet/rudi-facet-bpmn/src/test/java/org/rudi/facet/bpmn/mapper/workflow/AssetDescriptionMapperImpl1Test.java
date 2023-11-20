package org.rudi.facet.bpmn.mapper.workflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.processing.Generated;

import org.rudi.facet.bpmn.bean.AssetDescription1TestData;
import org.rudi.facet.bpmn.entity.workflow.AssetDescription1TestEntity;
import org.springframework.stereotype.Component;

@Generated(value = "org.mapstruct.ap.MappingProcessor", date = "2021-11-30T10:24:06+0100", comments = "version: 1.4.2.Final, compiler: javac, environment: Java 11 (Oracle Corporation)")
@Component
public class AssetDescriptionMapperImpl1Test implements AssetDescriptionMapper1Test {

	@Override
	public AssetDescription1TestEntity dtoToEntity(AssetDescription1TestData arg0) {
		if (arg0 == null) {
			return null;
		}

		AssetDescription1TestEntity testAssetDescriptionEntity = new AssetDescription1TestEntity();

		testAssetDescriptionEntity.setUuid(arg0.getUuid());
		testAssetDescriptionEntity.setProcessDefinitionKey(arg0.getProcessDefinitionKey());
		testAssetDescriptionEntity.setProcessDefinitionVersion(arg0.getProcessDefinitionVersion());
		testAssetDescriptionEntity.setStatus(arg0.getStatus());
		testAssetDescriptionEntity.setFunctionalStatus(arg0.getFunctionalStatus());
		testAssetDescriptionEntity.setInitiator(arg0.getInitiator());
		testAssetDescriptionEntity.setUpdator(arg0.getUpdator());
		testAssetDescriptionEntity.setCreationDate(arg0.getCreationDate());
		testAssetDescriptionEntity.setUpdatedDate(arg0.getUpdatedDate());
		testAssetDescriptionEntity.setDescription(arg0.getDescription());
		testAssetDescriptionEntity.setAssignee(arg0.getAssignee());
		testAssetDescriptionEntity.setA(arg0.getA());

		return testAssetDescriptionEntity;
	}

	@Override
	public void dtoToEntity(AssetDescription1TestData arg0, AssetDescription1TestEntity arg1) {
		if (arg0 == null) {
			return;
		}

		arg1.setUuid(arg0.getUuid());
		arg1.setProcessDefinitionKey(arg0.getProcessDefinitionKey());
		arg1.setProcessDefinitionVersion(arg0.getProcessDefinitionVersion());
		arg1.setStatus(arg0.getStatus());
		arg1.setFunctionalStatus(arg0.getFunctionalStatus());
		arg1.setInitiator(arg0.getInitiator());
		arg1.setUpdator(arg0.getUpdator());
		arg1.setCreationDate(arg0.getCreationDate());
		arg1.setUpdatedDate(arg0.getUpdatedDate());
		arg1.setDescription(arg0.getDescription());
		arg1.setAssignee(arg0.getAssignee());
		arg1.setA(arg0.getA());
	}

	@Override
	public List<AssetDescription1TestEntity> dtoToEntities(List<AssetDescription1TestData> arg0) {
		if (arg0 == null) {
			return null;
		}

		List<AssetDescription1TestEntity> list = new ArrayList<AssetDescription1TestEntity>(arg0.size());
		for (AssetDescription1TestData testAssetDescription : arg0) {
			list.add(dtoToEntity(testAssetDescription));
		}

		return list;
	}

	@Override
	public AssetDescription1TestData entityToDto(AssetDescription1TestEntity arg0) {
		if (arg0 == null) {
			return null;
		}

		AssetDescription1TestData testAssetDescription = new AssetDescription1TestData();

		testAssetDescription.setUuid(arg0.getUuid());
		testAssetDescription.setProcessDefinitionKey(arg0.getProcessDefinitionKey());
		testAssetDescription.setProcessDefinitionVersion(arg0.getProcessDefinitionVersion());
		testAssetDescription.setStatus(arg0.getStatus());
		testAssetDescription.setFunctionalStatus(arg0.getFunctionalStatus());
		testAssetDescription.setInitiator(arg0.getInitiator());
		testAssetDescription.setUpdator(arg0.getUpdator());
		testAssetDescription.setDescription(arg0.getDescription());
		testAssetDescription.setAssignee(arg0.getAssignee());
		testAssetDescription.setCreationDate(arg0.getCreationDate());
		testAssetDescription.setUpdatedDate(arg0.getUpdatedDate());
		testAssetDescription.setA(arg0.getA());

		return testAssetDescription;
	}

	@Override
	public List<AssetDescription1TestData> entitiesToDto(Collection<AssetDescription1TestEntity> arg0) {
		if (arg0 == null) {
			return null;
		}

		List<AssetDescription1TestData> list = new ArrayList<AssetDescription1TestData>(arg0.size());
		for (AssetDescription1TestEntity testAssetDescriptionEntity : arg0) {
			list.add(entityToDto(testAssetDescriptionEntity));
		}

		return list;
	}
}
