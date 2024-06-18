[#-------------- ASSIGNMENTS --------------]
[#include "/rudi/templates/macros/localized.ftl"/]
[#include "/rudi/templates/macros/normalized.ftl"/]
[#assign lang = ctx.getParameter('lang')!"fr"]
[#assign newsContent = ctx.newsContent!]

[#-------------- RENDERING ----------------]
[#if newsContent?has_content]
    <div class="news-container card-news-container">
        <link rel="stylesheet" type="text/css" href="${ctx.contextPath}/.resources/rudi/webresources/css/shared.css"/>
        <link rel="stylesheet" type="text/css" href="${ctx.contextPath}/.resources/rudi/webresources/css/news.css"/>

        [#assign asset = damfn.getAsset(newsContent.image1)!]
        [#if asset?has_content]
            [#assign url=damfn.getRendition(asset, "small-square").getLink()!]
        [#else]
            [#assign url=""]
        [/#if]
        [#assign newsContentNode = cmsfn.asJCRNode(newsContent)!]
        [#if newsContentNode?has_content]
            [#assign id = newsContent["jcr:uuid"]!]
            [#assign lastModifiedProperty = newsContentNode.getProperty("mgnl:lastModified")!]
            [#assign date = lastModifiedProperty.getDate()!]
            [#assign urlTitle = normalized(localized(newsContent,"title1",lang))!]
        [/#if]
        <a class="card-click-redirection" href="@self/news/${id!}/rudi-news@one-news-detailed/${urlTitle}">
            <div class="card-img-container">
                <img src="${url!}"/>
            </div>

            <div class="card-news-text-content">
                <div class="modified-at">${localized(newsContent,"modified",lang)} ${date?string("dd.MM.yyyy")}</div>

                <div class="card-news-title1">${localized(newsContent,"title1",lang)}</div>

                <div class="card-news-sumup-container">
                    <div class="card-news-sumup">
                        ${localized(newsContent,"sumup",lang)}
                    </div>
                </div>
            </div>

        </a>
    </div>

[/#if]