/**
 *
 */
package org.rudi.microservice.acl.service.mapper.accountregistration;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.rudi.common.service.mapper.AbstractMapper;
import org.rudi.common.service.mapper.MapperUtils;
import org.rudi.microservice.acl.core.bean.Account;
import org.rudi.microservice.acl.storage.entity.accountregistration.AccountRegistrationEntity;

/**
 * @author NTR18299
 *
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { MapperUtils.class })
public interface AccountRegistrationMapper extends AbstractMapper<AccountRegistrationEntity, Account> {

	@Override
	@InheritInverseConfiguration
	AccountRegistrationEntity dtoToEntity(Account dto);

	/**
	 * Converti un RoleEntity en Role.
	 *
	 * @param entity entity to transform to dto
	 * @return Account
	 */
	@Override
	Account entityToDto(AccountRegistrationEntity entity);

	@Override
	void dtoToEntity(Account dto, @MappingTarget AccountRegistrationEntity entity);

}
