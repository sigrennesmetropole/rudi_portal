package org.rudi.common.service.validator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.rudi.common.service.exception.AppServiceBadRequestException;

public abstract class AbstractValidator<T> {

	protected static final String GET_UUID_METHOD = "getUUID";

	protected void validateStringField(T dto, String methodName, String errorMessage)
			throws AppServiceBadRequestException {
		try {
			Method m = dto.getClass().getMethod(methodName);
			String code = (String) m.invoke(dto);
			if (StringUtils.isEmpty(code)) {
				throw new AppServiceBadRequestException(errorMessage);
			}
		} catch (IllegalArgumentException | NoSuchMethodException | SecurityException | IllegalAccessException
				| InvocationTargetException e) {
			// nothing to do
		}
	}

	protected void validateNotNullField(T dto, String methodName, String errorMessage)
			throws AppServiceBadRequestException {
		try {
			Method m = dto.getClass().getMethod(methodName);
			Object o = m.invoke(dto);
			if (o == null) {
				throw new AppServiceBadRequestException(errorMessage);
			}
		} catch (IllegalArgumentException | NoSuchMethodException | SecurityException | IllegalAccessException
				| InvocationTargetException e) {
			// nothing to do
		}
	}

	protected void validateNotNullSubField(T dto, String methodName, String subMethodName, String errorMessage)
			throws AppServiceBadRequestException {
		try {
			Method m = dto.getClass().getMethod(methodName);
			Object o = m.invoke(dto);
			if (o == null) {
				throw new AppServiceBadRequestException(errorMessage);
			}
			Method m2 = o.getClass().getMethod(subMethodName);
			Object o2 = m2.invoke(o);
			if (o2 == null) {
				throw new AppServiceBadRequestException(errorMessage);
			}
		} catch (IllegalArgumentException | NoSuchMethodException | SecurityException | IllegalAccessException
				| InvocationTargetException e) {
			// nothing to do
		}
	}

	protected void validateNotNullSubSubField(T dto, String methodName, String subMethodName, String subSubMethodName,
			String errorMessage) throws AppServiceBadRequestException {
		try {
			Method m = dto.getClass().getMethod(methodName);
			Object o = m.invoke(dto);
			if (o == null) {
				throw new AppServiceBadRequestException(errorMessage);
			}
			Method m2 = o.getClass().getMethod(subMethodName);
			Object o2 = m2.invoke(o);
			if (o2 == null) {
				throw new AppServiceBadRequestException(errorMessage);
			}
			Method m3 = o2.getClass().getMethod(subSubMethodName);
			Object o3 = m3.invoke(o2);
			if (o3 == null) {
				throw new AppServiceBadRequestException(errorMessage);
			}
		} catch (IllegalArgumentException | NoSuchMethodException | SecurityException | IllegalAccessException
				| InvocationTargetException e) {
			// nothing to do
		}
	}

	protected void validateRangeDateField(T dto, String methodName1, String methodName2, String errorMessage)
			throws AppServiceBadRequestException {
		try {
			Method m1 = dto.getClass().getMethod(methodName1);
			LocalDateTime d1 = (LocalDateTime) m1.invoke(dto);

			Method m2 = dto.getClass().getMethod(methodName2);
			LocalDateTime d2 = (LocalDateTime) m2.invoke(dto);
			if (d2 != null && d2.isBefore(d1)) {
				throw new AppServiceBadRequestException(errorMessage);
			}
		} catch (IllegalArgumentException | NoSuchMethodException | SecurityException | IllegalAccessException
				| InvocationTargetException e) {
			// nothing to do
		}
	}

	protected void validateMaxLengthField(T dto, String methodName, int length, String errorMessage)
			throws AppServiceBadRequestException {
		try {
			Method m1 = dto.getClass().getMethod(methodName);
			Object o1 = m1.invoke(dto);

			Method m2 = o1.getClass().getMethod("length");
			int objectLength = (int) m2.invoke(o1);

			if (objectLength > length) {
				throw new AppServiceBadRequestException(errorMessage);
			}
		} catch (IllegalArgumentException | NoSuchMethodException | SecurityException | IllegalAccessException
				| InvocationTargetException e) {
			// nothing to do
		}
	}

	protected void validateDoubleGreaterThanZero(T dto, String methodName1, String errorMessage)
			throws AppServiceBadRequestException {
		try {
			Method m1 = dto.getClass().getMethod(methodName1);
			Double d1 = (Double) m1.invoke(dto);
			if (d1 != null && (d1.isInfinite() || d1.isNaN() || d1 <= 0.0d)) {
				throw new AppServiceBadRequestException(errorMessage);
			}
		} catch (IllegalArgumentException | NoSuchMethodException | SecurityException | IllegalAccessException
				| InvocationTargetException e) {
			// nothing to do
		}
	}

	protected void validateDoubleGreaterOrEqualThanZero(T dto, String methodName1, String errorMessage)
			throws AppServiceBadRequestException {
		try {
			Method m1 = dto.getClass().getMethod(methodName1);
			Double d1 = (Double) m1.invoke(dto);
			if (d1 != null && (d1.isInfinite() || d1.isNaN() || d1 < 0.0d)) {
				throw new AppServiceBadRequestException(errorMessage);
			}
		} catch (IllegalArgumentException | NoSuchMethodException | SecurityException | IllegalAccessException
				| InvocationTargetException e) {
			// nothing to do
		}
	}

	protected void validateNotEmptyFieldList(T dto, String methodName1, String errorMessage)
			throws AppServiceBadRequestException {
		try {
			Method m1 = dto.getClass().getMethod(methodName1);
			Object o1 = m1.invoke(dto);
			if (!(o1 instanceof List<?>) || ((List<?>) o1).isEmpty()) {
				throw new AppServiceBadRequestException(errorMessage);
			}
		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ignored) {
			// nothing to do
		}
	}

	protected void validateNotNullSubFieldList(T dto, String methodName1, String methodName2, String errorMessage)
			throws AppServiceBadRequestException {
		try {
			Method m1 = dto.getClass().getMethod(methodName1);
			Object value = m1.invoke(dto);
			if (value instanceof List<?>) {
				for (Object o1 : ((List<?>) value)) {
					if (o1 == null) {
						throw new AppServiceBadRequestException(errorMessage);
					}
					Method m2 = o1.getClass().getMethod(methodName2);
					Object o2 = m2.invoke(o1);
					if (o2 == null) {
						throw new AppServiceBadRequestException(errorMessage);
					}
				}
			}
		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ignored) {
			// nothing to do
		}
	}
}
