package org.rudi.facet.cms.impl.utils.impl;

import org.rudi.facet.cms.impl.utils.ResourceUriRewriter;
import org.springframework.stereotype.Component;

@Component
public class ResourceCssLinkUriRewiter implements ResourceUriRewriter {

	@Override
	public String getAcceptedElement() {
		return "link[href]";
	}

	@Override
	public String getTargetAttribute() {
		return "href";
	}
}
