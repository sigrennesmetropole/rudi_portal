package org.rudi.common.service.exception;

import java.util.UUID;

import javax.annotation.Nullable;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.rudi.common.storage.entity.AbstractLongIdEntity;
import org.springframework.dao.EmptyResultDataAccessException;

import lombok.val;

/**
 * @author fni18300
 */
public class AppServiceNotFoundException extends AppServiceException {

	private static final long serialVersionUID = 5747244696042729320L;

	private <T> AppServiceNotFoundException(Class<T> entityClass, UUID entityUuid, @Nullable EmptyResultDataAccessException cause) {
		this(entityClass, "UUID", entityUuid, cause);
	}

	public <T> AppServiceNotFoundException(Class<T> entityClass, UUID entityUuid) {
		this(entityClass, entityUuid, null);
	}

	public <T> AppServiceNotFoundException(Class<T> entityClass, String identifier, Object value) {
		this(entityClass, identifier, value, null);
	}

	public AppServiceNotFoundException(AbstractLongIdEntity entity, EmptyResultDataAccessException cause) {
		this(entity.getClass(), entity.getUuid(), cause);
	}

	private <T> AppServiceNotFoundException(Class<T> entityClass, String identifier, Object value, @Nullable EmptyResultDataAccessException cause) {
		this(getEntityName(entityClass), identifier, value, cause);
	}

	public AppServiceNotFoundException(String entityName, String identifier, Object value) {
		this(entityName, identifier, value, null);
	}

	private AppServiceNotFoundException(String entityName, String identifier, Object value, @Nullable EmptyResultDataAccessException cause) {
		super(getMessage(entityName, identifier, value), cause, AppServiceExceptionsStatus.NOT_FOUND);
	}

	public AppServiceNotFoundException(EmptyResultDataAccessException cause) {
		super(cause.getMessage(), AppServiceExceptionsStatus.NOT_FOUND);
	}

	private static String getMessage(String entityName, String identifier, Object value) {
		return String.format("%s with %s = \"%s\" not found", entityName, identifier, value);
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
