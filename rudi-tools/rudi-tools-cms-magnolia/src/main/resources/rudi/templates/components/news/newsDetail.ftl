[#-------------- ASSIGNMENTS --------------]
[#include "/rudi/templates/macros/localized.ftl"/]
[#assign lang = ctx.getParameter('lang')!"fr"]
[#assign newsContent = ctx.newsContent!]

[#-------------- RENDERING --------------]

[#if newsContent?has_content]
<div class="news-container">
        <h2 class="news-title1">${localized(newsContent,"title1",lang)!}</h2>
        <h3 class="news-title2">${localized(newsContent,"title2",lang)!}</h3>

        [#assign asset = damfn.getAsset(newsContent.image2)!]
        [#if asset?has_content]
        	[#assign url=damfn.getRendition(asset, "small-square").getLink()!]
        [#else]
        	[#assign url=""]
        [/#if]

        <div class="news-body" style="background-image: url(${url});" >
                ${localized(newsContent,"body",lang)!}
        </div>
</div>
[/#if]
