package org.rudi.microservice.projekt.storage.dao.reutilisationstatus;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.rudi.microservice.projekt.core.bean.ReutilisationStatusSearchCriteria;
import org.rudi.microservice.projekt.storage.entity.ReutilisationStatusEntity;

public interface ReutilisationStatusCustomDao {
	Page<ReutilisationStatusEntity> searchReutilisationStatus(ReutilisationStatusSearchCriteria criteria, Pageable pageable);
}
