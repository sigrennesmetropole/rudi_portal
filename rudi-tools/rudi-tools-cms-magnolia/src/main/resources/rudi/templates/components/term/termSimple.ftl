[#-------------- ASSIGNMENTS --------------]
[#include "/rudi/templates/macros/localized.ftl"/]
[#assign lang = ctx.getParameter('lang')!"fr"]
[#assign termContent = ctx.termContent!]

[#-------------- RENDERING --------------]

[#if termContent?has_content]
<div class="term-container">
	<a class="term-link" href="${cmsfn.link(termContent)}">${localized(termContent,"link",lang)}</a>
</div>	
[/#if]

