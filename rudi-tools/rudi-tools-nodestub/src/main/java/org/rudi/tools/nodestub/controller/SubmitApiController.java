/**
 * RUDI Portail
 */
package org.rudi.tools.nodestub.controller;

import javax.validation.Valid;

import org.rudi.microservice.kalim.core.bean.Method;
import org.rudi.tools.nodestub.component.KalimHelper;
import org.rudi.tools.nodestub.controller.api.SubmitApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author FNI18300
 *
 */
@RestController
public class SubmitApiController implements SubmitApi {

	@Autowired
	private KalimHelper kalimHelper;

	public ResponseEntity<Void> submitMetadata(Method method, @Valid org.rudi.facet.kaccess.bean.Metadata body)
			throws Exception {
		kalimHelper.submit(method, body);
		return ResponseEntity.ok().build();
	}
}
