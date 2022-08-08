package org.rudi.common.service.helper;

import org.rudi.common.service.exception.AppServiceException;

@FunctionalInterface
public interface Processor<E> {
	void process(E element) throws AppServiceException;
}
