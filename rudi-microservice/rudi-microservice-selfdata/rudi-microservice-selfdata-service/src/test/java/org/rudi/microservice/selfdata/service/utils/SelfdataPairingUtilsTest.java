package org.rudi.microservice.selfdata.service.utils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.facet.kaccess.bean.MatchingData;
import org.rudi.microservice.selfdata.core.bean.MatchingField;
import org.rudi.microservice.selfdata.service.SelfdataSpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.RequiredArgsConstructor;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SelfdataSpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SelfdataPairingUtilsTest {

	private final SelfdataPairingUtils selfdataPairingUtils;

	@Test
	public void test_matchingData_empty_on_empty() throws AppServiceException {
		List<MatchingField> fields = selfdataPairingUtils.extractMatchingDataFromFormData(Collections.emptyMap(),
				Collections.emptyList());
		assertTrue(fields.isEmpty());
	}

	@Test
	public void test_matchingData_non_required() throws AppServiceException {

		// 2 matching datas requises dont 1 optionnelle
		MatchingData required = new MatchingData()
				.code("ONE")
				.required(true);

		MatchingData optional = new MatchingData()
				.code("TWO")
				.required(false);

		List<MatchingData> matchingDatas = List.of(required, optional);

		// L'utilisateur ne renseigne que la donnée requise
		Map<String, Object> saisie = Map.of("ONE", "pivot requis");

		List<MatchingField> fields = selfdataPairingUtils.extractMatchingDataFromFormData(saisie, matchingDatas);

		// Le traitement est OK et on récupère que le champ requis
		assertEquals(1, fields.size());
		assertEquals("ONE", fields.get(0).getCode());
	}
}
