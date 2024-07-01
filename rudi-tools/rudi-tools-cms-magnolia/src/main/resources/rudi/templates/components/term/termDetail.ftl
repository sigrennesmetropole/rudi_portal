[#-------------- ASSIGNMENTS --------------]
[#include "/rudi/templates/macros/localized.ftl"/]
[#assign lang = ctx.getParameter('lang')!"fr"]
[#assign termContent = ctx.termContent!]

[#-------------- RENDERING --------------]

[#if termContent?has_content]
    <div class="term-container">
        <link rel="stylesheet" type="text/css" href="${ctx.contextPath}/.resources/rudi/webresources/css/shared.css"/>

        [#assign termsContentNode = cmsfn.asJCRNode(termContent)!]
        [#if termsContentNode?has_content]
            [#assign id = termContent["jcr:uuid"]!]
            [#assign lastModifiedProperty = termsContentNode.getProperty("mgnl:lastModified")!]
            [#assign date = lastModifiedProperty.getDate()!]
        [/#if]
        <div class="title-detail">${localized(termContent,"title",lang)}</div>
        <div class="modified-at">${localized(termContent,"modified",lang)} ${date?string("dd.MM.yyyy")}</div>

        <div class="content-detail">
            ${localized(termContent,"body",lang)}
        </div>
    </div>
[/#if]