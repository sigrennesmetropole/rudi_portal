package org.rudi.facet.cms.impl.utils.impl;

import org.rudi.facet.cms.impl.utils.ResourceUriRewriter;
import org.springframework.stereotype.Component;

@Component
public class ResourceAnchorLinkUriRewiter implements ResourceUriRewriter {

	@Override
	public String getAcceptedElement() {
		return "a[href]";
	}


	@Override
	public String getTargetAttribute() {
		return "href";
	}
}
