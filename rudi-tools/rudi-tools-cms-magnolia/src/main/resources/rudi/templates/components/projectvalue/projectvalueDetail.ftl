[#-------------- ASSIGNMENTS --------------]
[#include "/rudi/templates/macros/localized.ftl"/]
[#assign lang = ctx.getParameter('lang')!"fr"]
[#assign projectvalueContent = ctx.projectvalueContent!]

[#-------------- RENDERING ----------------]
[#if projectvalueContent?has_content]
    <div class="projectvalue-container">
        <link rel="stylesheet" type="text/css" href="${ctx.contextPath}/.resources/rudi/webresources/css/shared.css"/>

        [#assign asset = damfn.getAsset(projectvalueContent.image)!]
        [#if asset?has_content]
            [#assign image_url=damfn.getRendition(asset, "small-square").getLink()!]
            [#assign image_caption=damfn.getAssetMap(asset).title!]
        [#else]
            [#assign image_url=""]
            [#assign image_caption=""]
        [/#if]

        [#assign newsContentNode = cmsfn.asJCRNode(projectvalueContent)!]
        [#if newsContentNode?has_content]
            [#assign id = projectvalueContent["jcr:uuid"]!]
            [#assign lastModifiedProperty = newsContentNode.getProperty("mgnl:lastModified")!]
            [#assign date = lastModifiedProperty.getDate()!]
        [/#if]

        <div class="title1-detail">${localized(projectvalueContent,"shorttitle",lang)}</div>
        <div class="title2-detail">${localized(projectvalueContent,"title",lang)}</div>
        <div class="modified-at">${localized(projectvalueContent,"modified",lang)} ${date?string("dd.MM.yyyy")}</div>

        <div class="img-container-detail">
            <img class="img-detail" src="${image_url!}"/>
            <div class="img-caption-detail">${image_caption!}</div>
        </div>

        <div class="content-detail">
            ${localized(projectvalueContent,"body",lang)}
        </div>

    </div>
[/#if]
