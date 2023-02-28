package org.rudi.tools.nodestub.config.filter;

import org.rudi.common.facade.config.filter.JwtRequestFilter;
import org.rudi.common.facade.config.filter.JwtTokenData;
import org.rudi.common.service.helper.UtilContextHelper;

public class NodestubJwtRequestFilter extends JwtRequestFilter {

	NodestubJwtTokenUtil nodestubJwtTokenUtil;

	public NodestubJwtRequestFilter(
			String[] excludeUrlPatterns,
			UtilContextHelper utilContextHelper,
			NodestubJwtTokenUtil nodestubJwtTokenUtil) {
		super(excludeUrlPatterns, utilContextHelper);
		this.nodestubJwtTokenUtil = nodestubJwtTokenUtil;
	}

	@Override
	public JwtTokenData validateToken(String requestAuthentTokenHeader) {
		return nodestubJwtTokenUtil.validateToken(requestAuthentTokenHeader);
	}
}
