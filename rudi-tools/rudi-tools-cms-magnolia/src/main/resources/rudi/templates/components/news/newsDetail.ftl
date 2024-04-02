[#-------------- ASSIGNMENTS --------------]
[#include "/rudi/templates/macros/localized.ftl"/]
[#assign lang = ctx.getParameter('lang')!"fr"]
[#assign newsContent = ctx.newsContent!]

[#-------------- RENDERING --------------]


[#if newsContent?has_content]

[#assign currentSite=sitefn.site(newsContent)]
s=${currentSite}
[#assign site=sitefn.site()]
<br/>ds=${site}
[#assign currentTheme=sitefn.theme(sitefn.site(newsContent))]
<br/>t=${currentTheme}
<br/>t=${currentTheme.getName()!}
cssfiles =
[#list currentTheme.getCssFiles() as cssFile]
        ${cssFile}
[/#list]

<div class="news-container">
        <h2 class="news-title1">${localized(newsContent,"title1",lang)!}</h2>
        <h3 class="news-title2">${localized(newsContent,"title2",lang)!}</h3>
  		
  		[#assign newsContentNode = cmsfn.asJCRNode(newsContent)!]
  		<div class="news-date-author">
        	[#if newsContentNode?has_content]
        		[#assign lastModified = newsContentNode.getProperty("mgnl:lastModified")!]
            	[#assign lastModifiedDate = lastModified.getDate()!]
            	<span class="news-date">${lastModifiedDate?string("dd.MM.yyyy")}</span>
        	[/#if]
        	[#if newsContent.author?has_content]
        		<span class="news-author">${newsContent.author}</span>
        	[/#if]
        </div>

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
