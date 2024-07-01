package org.rudi.facet.cms.impl.utils.impl;

import org.rudi.facet.cms.impl.utils.ResourceUriRewriter;
import org.springframework.stereotype.Component;

@Component
public class ResourceImageBackgroundUriRewriter implements ResourceUriRewriter {

	@Override
	public String getAcceptedElement() {
		return "[style]";
	}

	@Override
	public String getTargetAttribute() {
		return "style";
	}
}
