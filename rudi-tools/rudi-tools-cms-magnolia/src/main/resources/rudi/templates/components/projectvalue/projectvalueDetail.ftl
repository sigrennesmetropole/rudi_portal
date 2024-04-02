[#-------------- ASSIGNMENTS --------------]
[#include "/rudi/templates/macros/localized.ftl"/]
[#assign lang = ctx.getParameter('lang')!"fr"]
[#assign projectvalueContent = ctx.projectvalueContent!]

[#-------------- RENDERING --------------]

[#if projectvalueContent?has_content]
<div class="projectvalue-container">
	<h2 class="projectvalue-title">${localized(projectvalueContent,"title",lang)}</h2>
	
	[#assign asset = damfn.getAsset(projectvalueContent.image)!]
	[#if asset?has_content]
        [#assign url=damfn.getRendition(asset, "small-square").getLink()!]
    [#else]
        [#assign url=""]
    [/#if]
	
	<div class="projectvalue-body" style="background-image: url(${url});" >
		${localized(projectvalueContent,"body",lang)}
	</div>
</div>	
[/#if]

