[#-------------- ASSIGNMENTS --------------]
[#include "/rudi/templates/macros/localized.ftl"/]
[#assign lang = ctx.getParameter('lang')!"fr"]
[#assign termContent = ctx.termContent!]

[#-------------- RENDERING --------------]

[#if termContent?has_content]
<div class="term-container">
	<h2 class="term-title">${localized(termContent,"title",lang)}</h2>
	<div class="term-body">
		${localized(termContent,"body",lang)}
	</div>
</div>	
[/#if]

