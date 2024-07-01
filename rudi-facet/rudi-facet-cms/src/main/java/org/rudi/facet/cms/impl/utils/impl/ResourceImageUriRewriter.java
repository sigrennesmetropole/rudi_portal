package org.rudi.facet.cms.impl.utils.impl;

import org.rudi.facet.cms.impl.utils.ResourceUriRewriter;
import org.springframework.stereotype.Component;

@Component
public class ResourceImageUriRewriter implements ResourceUriRewriter {

	@Override
	public String getAcceptedElement() {
		return "img";
	}

	@Override
	public String getTargetAttribute() {
		return "src";
	}
}
