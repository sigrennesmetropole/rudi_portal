package org.rudi.common.service.exception;

import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.rudi.common.storage.entity.AbstractLongIdEntity;
import org.springframework.dao.EmptyResultDataAccessException;

import javax.persistence.Table;
import java.util.UUID;

/**
 * @author fni18300
 */
public class AppServiceNotFoundException extends AppServiceException {

	private static final long serialVersionUID = 5747244696042729320L;

	private <T> AppServiceNotFoundException(Class<T> entityClass, UUID entityUuid, EmptyResultDataAccessException cause) {
		super(getMessage(entityUuid, entityClass), cause, AppServiceExceptionsStatus.NOT_FOUND);
	}

	public <T> AppServiceNotFoundException(Class<T> entityClass, UUID entityUuid) {
		super(getMessage(entityUuid, entityClass), AppServiceExceptionsStatus.NOT_FOUND);
	}

	public <T> AppServiceNotFoundException(AbstractLongIdEntity entity, EmptyResultDataAccessException cause) {
		this(entity.getClass(), entity.getUuid(), cause);
	}

	public <T> AppServiceNotFoundException(EmptyResultDataAccessException cause) {
		super(cause.getMessage(), AppServiceExceptionsStatus.NOT_FOUND);
	}

	private static <T> String getMessage(UUID entityUuid, Class<T> entityClass) {
		return String.format("%s with UUID = \"%s\" not found", getEntityName(entityClass), entityUuid);
	}

	private static <T> String getEntityName(Class<T> entityClass) {
		val tableAnnotation = entityClass.getAnnotation(Table.class);
		if (tableAnnotation != null) {
			final String tableName = tableAnnotation.name();
			if (StringUtils.isNotEmpty(tableName)) {
				return tableName;
			}
		}
		return entityClass.getSimpleName();
	}

}
