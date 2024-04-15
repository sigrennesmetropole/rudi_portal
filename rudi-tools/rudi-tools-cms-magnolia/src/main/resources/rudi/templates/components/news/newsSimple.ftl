[#-------------- ASSIGNMENTS --------------]
[#include "/rudi/templates/macros/localized.ftl"/]
[#include "/rudi/templates/macros/normalized.ftl"/]
[#assign lang = ctx.getParameter('lang')!"fr"]
[#assign newsContent = ctx.newsContent!]

[#-------------- RENDERING ----------------]
[#if newsContent?has_content]
    <div class="news-container">
        <link rel="stylesheet" type="text/css" href="${ctx.contextPath}/.resources/rudi/webresources/css/shared.css"/>
        <link rel="stylesheet" type="text/css" href="${ctx.contextPath}/.resources/rudi/webresources/css/news.css"/>

        [#assign newsContentNode = cmsfn.asJCRNode(newsContent)!]
        [#if newsContentNode?has_content]
            [#assign id = newsContent["jcr:uuid"]!]
            [#assign lastModifiedProperty = newsContentNode.getProperty("mgnl:lastModified")!]
            [#assign date = lastModifiedProperty.getDate()!]
            [#assign urlTitle = normalized(localized(newsContent,"title1",lang))!]
        [/#if]

        <div class="modified-at">${localized(newsContent,"modified",lang)} ${date?string("dd.MM.yyyy")}</div>

        <div class="news-title1">${localized(newsContent,"title1",lang)}</div>

        <div class="news-sumup-container">
            <div class="news-sumup">
                ${localized(newsContent,"sumup",lang)}
            </div>
        </div>

        <div class="btn-container">
            <a class="btn"
               href="@self/news/${id!}/rudi-news@one-news-detailed/${urlTitle}">
                ${localized(newsContent,"link",lang)!}
            </a>
        </div>
    </div>
[/#if]