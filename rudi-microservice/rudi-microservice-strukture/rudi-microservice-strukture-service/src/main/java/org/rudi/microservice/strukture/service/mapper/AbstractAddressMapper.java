/**
 * 
 */
package org.rudi.microservice.strukture.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.rudi.common.service.mapper.AbstractMapper;
import org.rudi.common.service.mapper.MapperUtils;
import org.rudi.microservice.strukture.core.bean.AbstractAddress;
import org.rudi.microservice.strukture.core.bean.EmailAddress;
import org.rudi.microservice.strukture.core.bean.PostalAddress;
import org.rudi.microservice.strukture.core.bean.TelephoneAddress;
import org.rudi.microservice.strukture.core.bean.WebsiteAddress;
import org.rudi.microservice.strukture.storage.entity.address.AbstractAddressEntity;
import org.rudi.microservice.strukture.storage.entity.address.EmailAddressEntity;
import org.rudi.microservice.strukture.storage.entity.address.PostalAddressEntity;
import org.rudi.microservice.strukture.storage.entity.address.TelephoneAddressEntity;
import org.rudi.microservice.strukture.storage.entity.address.WebsiteAddressEntity;

/**
 * @author FNI18300
 *
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { MapperUtils.class,
		AddressRoleMapper.class, EmailAddressMapper.class, PostalAddressMapper.class, TelephoneAddressMapper.class,
		WebsiteAddressMapper.class })
public interface AbstractAddressMapper extends AbstractMapper<AbstractAddressEntity, AbstractAddress> {

	@Override
	default AbstractAddressEntity dtoToEntity(AbstractAddress dto) {
		if (dto == null) {
			return null;
		} else {
			AbstractAddressEntity entity = null;
			switch (dto.getType()) {
			case WEBSITE:
				entity = getWebsiteAddressMapper().dtoToEntity((WebsiteAddress) dto);
				break;
			case EMAIL:
				entity = getEmailAddressMapper().dtoToEntity((EmailAddress) dto);
				break;
			case PHONE:
				entity = Mappers.getMapper(TelephoneAddressMapper.class).dtoToEntity((TelephoneAddress) dto);
				break;
			case POSTAL:
				entity = Mappers.getMapper(PostalAddressMapper.class).dtoToEntity((PostalAddress) dto);
				break;
			default:
				break;
			}
			return entity;
		}
	}

	private EmailAddressMapper getEmailAddressMapper() {
		return Mappers.getMapper(EmailAddressMapper.class);
	}

	private WebsiteAddressMapper getWebsiteAddressMapper() {
		return Mappers.getMapper(WebsiteAddressMapper.class);
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
			case WEBSITE:
				dto = getWebsiteAddressMapper().entityToDto((WebsiteAddressEntity) entity);
				break;
			case EMAIL:
				dto = getEmailAddressMapper().entityToDto((EmailAddressEntity) entity);
				break;
			case PHONE:
				dto = Mappers.getMapper(TelephoneAddressMapper.class).entityToDto((TelephoneAddressEntity) entity);
				break;
			case POSTAL:
				dto = Mappers.getMapper(PostalAddressMapper.class).entityToDto((PostalAddressEntity) entity);
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
			case WEBSITE:
				getWebsiteAddressMapper().dtoToEntity((WebsiteAddress) dto, (WebsiteAddressEntity) entity);
				break;
			case EMAIL:
				getEmailAddressMapper().dtoToEntity((EmailAddress) dto, (EmailAddressEntity) entity);
				break;
			case PHONE:
				Mappers.getMapper(TelephoneAddressMapper.class).dtoToEntity((TelephoneAddress) dto, (TelephoneAddressEntity) entity);
				break;
			case POSTAL:
				Mappers.getMapper(PostalAddressMapper.class).dtoToEntity((PostalAddress) dto, (PostalAddressEntity) entity);
				break;
			default:
				break;
			}
		}
	}

}
