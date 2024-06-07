/**
 * 
 */
package org.rudi.microservice.acl.service.mapper.address;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.rudi.common.service.mapper.AbstractMapper;
import org.rudi.common.service.mapper.MapperUtils;
import org.rudi.microservice.acl.core.bean.AbstractAddress;
import org.rudi.microservice.acl.core.bean.EmailAddress;
import org.rudi.microservice.acl.core.bean.PostalAddress;
import org.rudi.microservice.acl.core.bean.TelephoneAddress;
import org.rudi.microservice.acl.storage.entity.address.AbstractAddressEntity;
import org.rudi.microservice.acl.storage.entity.address.EmailAddressEntity;
import org.rudi.microservice.acl.storage.entity.address.PostalAddressEntity;
import org.rudi.microservice.acl.storage.entity.address.TelephoneAddressEntity;

/**
 * @author FNI18300
 *
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { MapperUtils.class,
		AddressRoleMapper.class, EmailAddressMapper.class, PostalAddressMapper.class, TelephoneAddressMapper.class })
public interface AbstractAddressMapper extends AbstractMapper<AbstractAddressEntity, AbstractAddress> {

	private EmailAddressMapper getEmailAddressMapper() {
		return Mappers.getMapper(EmailAddressMapper.class);
	}

	private PostalAddressMapper getPostalAddressMapper() {
		return Mappers.getMapper(PostalAddressMapper.class);
	}

	private TelephoneAddressMapper getTelephoneAddressMapper() {
		return Mappers.getMapper(TelephoneAddressMapper.class);
	}

	@Override
	default AbstractAddressEntity dtoToEntity(AbstractAddress dto) {
		if (dto == null) {
			return null;
		} else {
			AbstractAddressEntity entity = null;
			switch (dto.getType()) {
			case EMAIL:
				entity = getEmailAddressMapper().dtoToEntity((EmailAddress) dto);
				break;
			case PHONE:
				entity = getTelephoneAddressMapper().dtoToEntity((TelephoneAddress) dto);
				break;
			case POSTAL:
				entity = getPostalAddressMapper().dtoToEntity((PostalAddress) dto);
				break;
			default:
				break;
			}
			return entity;
		}
	}

	/**
	 * Converti un dossier en DossierDto.
	 *
	 * @param entity entity to transform to dto
	 * @return DossierDto
	 */
	@Override
	default AbstractAddress entityToDto(AbstractAddressEntity entity) {
		if (entity == null) {
			return null;
		} else {
			AbstractAddress dto = null;
			switch (entity.getType()) {
			case EMAIL:
				dto = getEmailAddressMapper().entityToDto((EmailAddressEntity) entity);
				break;
			case PHONE:
				dto = getTelephoneAddressMapper().entityToDto((TelephoneAddressEntity) entity);
				break;
			case POSTAL:
				dto = getPostalAddressMapper().entityToDto((PostalAddressEntity) entity);
				break;
			default:
				break;
			}
			return dto;
		}
	}

	default void dtoToEntity(AbstractAddress dto, @MappingTarget AbstractAddressEntity entity) {
		if (dto != null) {
			switch (dto.getType()) {
			case EMAIL:
				getEmailAddressMapper().dtoToEntity((EmailAddress) dto, (EmailAddressEntity) entity);
				break;
			case PHONE:
				getTelephoneAddressMapper().dtoToEntity((TelephoneAddress) dto, (TelephoneAddressEntity) entity);
				break;
			case POSTAL:
				getPostalAddressMapper().dtoToEntity((PostalAddress) dto, (PostalAddressEntity) entity);
				break;
			default:
				break;
			}
		}
	}

}
