[#-------------- ASSIGNMENTS --------------]
[#include "/rudi/templates/macros/localized.ftl"/]
[#include "/rudi/templates/macros/normalized.ftl"/]
[#assign lang = ctx.getParameter('lang')!"fr"]
[#assign termsContent = ctx.termContent!]

[#-------------- RENDERING --------------]

[#if termsContent?has_content]
<div class="term-container">
	<link rel="stylesheet" type="text/css" href="${ctx.contextPath}/.resources/rudi/webresources/css/shared.css"/>
	<link rel="stylesheet" type="text/css" href="${ctx.contextPath}/.resources/rudi/webresources/css/terms.css"/>

	[#assign termsContentNode = cmsfn.asJCRNode(termsContent)!]
	[#if termsContentNode?has_content]
		[#assign id = termsContent["jcr:uuid"]!]
		[#assign urlTitle = normalized(localized(termsContent,"title",lang))!]
	[/#if]

	<a class="term-link"
	   href="@self/terms/${id!}/rudi-terms@one-term-detailed/${urlTitle}">
		${localized(termsContent,"link",lang)!}
	</a>
</div>	
[/#if]

