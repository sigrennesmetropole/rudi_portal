package org.rudi.tools.nodestub.config.filter;

import org.rudi.common.facade.config.filter.JwtRequestFilter;
import org.rudi.common.facade.config.filter.JwtTokenData;
import org.rudi.common.service.helper.UtilContextHelper;
import org.springframework.web.client.RestTemplate;

public class NodestubJwtRequestFilter extends JwtRequestFilter {

	NodestubJwtTokenUtil nodestubJwtTokenUtil;

	public NodestubJwtRequestFilter(
			String[] excludeUrlPatterns,
			UtilContextHelper utilContextHelper,
			NodestubJwtTokenUtil nodestubJwtTokenUtil,
			RestTemplate oAuth2RestTemplate) {
		super(excludeUrlPatterns, utilContextHelper, oAuth2RestTemplate);
		this.nodestubJwtTokenUtil = nodestubJwtTokenUtil;
	}

	@Override
	public JwtTokenData validateToken(String requestAuthentTokenHeader) {
		return nodestubJwtTokenUtil.validateToken(requestAuthentTokenHeader);
	}
}
