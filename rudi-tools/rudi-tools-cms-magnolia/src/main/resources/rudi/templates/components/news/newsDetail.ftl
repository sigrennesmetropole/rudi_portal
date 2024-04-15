[#-------------- ASSIGNMENTS --------------]
[#include "/rudi/templates/macros/localized.ftl"/]
[#assign lang = ctx.getParameter('lang')!"fr"]
[#assign newsContent = ctx.newsContent!]

[#-------------- RENDERING ----------------]
[#if newsContent?has_content]
    <div class="news-container">
        <link rel="stylesheet" type="text/css" href="${ctx.contextPath}/.resources/rudi/webresources/css/shared.css"/>
        <link rel="stylesheet" type="text/css" href="${ctx.contextPath}/.resources/rudi/webresources/css/news.css"/>

        [#assign asset = damfn.getAsset(newsContent.image2)!]
        [#if asset?has_content]
            [#assign image_url=damfn.getRendition(asset, "small-square").getLink()!]
            [#assign image_caption=damfn.getAssetMap(asset).title!]
        [#else]
            [#assign image_url=""]
            [#assign image_caption=""]
        [/#if]

        [#assign newsContentNode = cmsfn.asJCRNode(newsContent)!]
        [#if newsContentNode?has_content]
            [#assign id = newsContent["jcr:uuid"]!]
            [#assign lastModifiedProperty = newsContentNode.getProperty("mgnl:lastModified")!]
            [#assign date = lastModifiedProperty.getDate()!]
        [/#if]

        <div class="title1-detail">${localized(newsContent,"title1",lang)}</div>
        <div class="title2-detail">${localized(newsContent,"title2",lang)}</div>

        <div class="modified-at">${localized(newsContent,"modified",lang)} ${date?string("dd.MM.yyyy")}</div>

        <div class="img-container-detail">
            <img class="img-detail" src="${image_url!}"/>
            <div class="img-caption-detail">${image_caption!}</div>
        </div>

        <div class="content-detail">
            ${localized(newsContent,"body",lang)}
        </div>

    </div>
[/#if]