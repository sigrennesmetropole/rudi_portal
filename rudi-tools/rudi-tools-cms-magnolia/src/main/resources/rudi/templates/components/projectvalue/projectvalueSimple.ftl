[#-------------- ASSIGNMENTS --------------]
[#include "/rudi/templates/macros/localized.ftl"/]
[#assign lang = ctx.getParameter('lang')!"fr"]
[#assign projectvalueContent = ctx.projectvalueContent!]

[#-------------- RENDERING --------------]

[#if projectvalueContent?has_content]
<div class="projectvalue-container">
	[#assign asset = damfn.getAsset(projectvalueContent.shortimage)!]
	[#if asset?has_content]
        [#assign url=damfn.getRendition(asset, "small-square").getLink()!]
    [#else]
        [#assign url=""]
    [/#if]
	
	<h2 class="projectvalue-shorttitle" style="background-image: url(${url});">${(localized(projectvalueContent,"shorttitle",lang)}</h2>

</div>	
[/#if]

