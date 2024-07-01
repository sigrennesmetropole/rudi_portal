package org.rudi.facet.cms.impl.utils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.CollectionUtils;
import org.jsoup.nodes.Element;

public interface ResourceUriRewriter {

	/**
	 *
	 * @return accepted html element
	 */
	String getAcceptedElement();

	/**
	 *
	 * @return target html attribute
	 */
	String getTargetAttribute();

	default boolean accept(Element element) {
		return element.is(getAcceptedElement());
	}

	default void compute(Element element, List<String> regexes, String routeToAdd) {
		String uri = element.attr(getTargetAttribute());
		if (CollectionUtils.isNotEmpty(regexes)) {
			for (String regex : regexes) {
				uri = rewriteUri(uri, regex, routeToAdd);
			}
		}
		element.attr(getTargetAttribute(), uri);
	}

	default String rewriteUri(String base, String regex, String routeToAdd) {
		Pattern pattern = Pattern.compile("(" + regex + ")");
		Matcher matcher = pattern.matcher(base);
		String value = matcher.find() ? matcher.group(1) : base;

		return base.replaceAll(regex, String.format(routeToAdd, value));
	}
}
