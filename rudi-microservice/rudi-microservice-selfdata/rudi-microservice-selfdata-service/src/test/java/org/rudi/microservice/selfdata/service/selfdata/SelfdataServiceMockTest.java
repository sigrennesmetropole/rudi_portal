package org.rudi.microservice.selfdata.service.selfdata;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rudi.common.core.security.AuthenticatedUser;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceNotFoundException;
import org.rudi.common.service.exception.AppServiceUnauthorizedException;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.apimaccess.exception.ApplicationOperationException;
import org.rudi.facet.apimaccess.helper.registration.RegistrationHelper;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.service.dataset.DatasetService;
import org.rudi.microservice.selfdata.service.selfdata.impl.SelfdataServiceImpl;

@ExtendWith(MockitoExtension.class)
class SelfdataServiceMockTest {

	@InjectMocks
	private SelfdataServiceImpl selfdataService;

	@Mock
	private DatasetService datasetService;

	@Mock
	private RegistrationHelper registrationHelper;

	@Mock
	private UtilContextHelper utilContextHelper;

	@Test
	void test_get_gdata_data_404_on_non_existing_dataset() throws DataverseAPIException {
		UUID randomUuid = UUID.randomUUID();
		when(datasetService.getDataset(randomUuid)).thenThrow(DataverseAPIException.class);
		assertThrows(AppServiceNotFoundException.class, () -> selfdataService.getGdataData(randomUuid));
	}

	@Test
	void test_get_gdata_data_401_on_unknownUser() throws DataverseAPIException {
		UUID randomUuid = UUID.randomUUID();
		when(datasetService.getDataset(randomUuid)).thenReturn(new Metadata());
		when(utilContextHelper.getAuthenticatedUser()).thenReturn(null);
		assertThrows(AppServiceUnauthorizedException.class, () -> selfdataService.getGdataData(randomUuid));
	}

	@Test
	void test_get_gdata_data_500_on_searchApplicationWso2Error()
			throws DataverseAPIException, ApplicationOperationException {
		UUID randomUuid = UUID.randomUUID();
		when(datasetService.getDataset(randomUuid)).thenReturn(new Metadata());
		AuthenticatedUser correctUser = new AuthenticatedUser();
		correctUser.setLogin("something@com");
		when(utilContextHelper.getAuthenticatedUser()).thenReturn(correctUser);
		when(registrationHelper.getOrCreateApplicationForUser(correctUser.getLogin()))
				.thenThrow(ApplicationOperationException.class);
		assertThrows(AppServiceException.class, () -> selfdataService.getGdataData(randomUuid));
	}

}
