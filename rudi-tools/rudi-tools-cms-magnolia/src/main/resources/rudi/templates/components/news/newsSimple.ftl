[#-------------- ASSIGNMENTS --------------]
[#include "/rudi/templates/macros/localized.ftl"/]
[#assign lang = ctx.getParameter('lang')!"fr"]
[#assign newsContent = ctx.newsContent!]

[#-------------- RENDERING --------------]

[#if newsContent?has_content]
<div class="news-container">
	<h2 class="news-title1">${localized(newsContent,"title1",lang)}</h2>
	
	[#assign asset = damfn.getAsset(newsContent.image1)!]
    [#if asset?has_content]
	    [#assign url=damfn.getRendition(asset, "small-square").getLink()!]
    [#else]
    	[#assign url=""]
    [/#if]
	
	<div class="news-sumup" style="background-image: url(${url});" >
		${localized(newsContent,"sumup",lang)}
	</div>
</div>	
[/#if]

