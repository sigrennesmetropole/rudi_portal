[#-------------- ASSIGNMENTS --------------]
[#include "/rudi/templates/macros/localized.ftl"/]
[#include "/rudi/templates/macros/normalized.ftl"/]
[#assign lang = ctx.getParameter('lang')!"fr"]
[#assign projectvalueContent = ctx.projectvalueContent!]

[#-------------- RENDERING --------------]
[#if projectvalueContent?has_content]

    [#assign projectvalueNode = cmsfn.asJCRNode(projectvalueContent)!]

    [#if projectvalueNode?has_content]
        [#assign id = projectvalueContent["jcr:uuid"]!]
        [#assign urlTitle = normalized(localized(projectvalueContent,"title",lang))!]
        [#assign asset = damfn.getAsset(projectvalueContent.shortimage)!]
        [#if asset?has_content]
            [#assign url=damfn.getRendition(asset, "small-square").getLink()!]
            [#assign id=projectvalueContent['jcr:uuid']!]
        [#else]
            [#assign url=""]
        [/#if]
    [/#if]


    <div class="projectvalue-container">
        <div class="projectvalue-shorttitle">
                ${(localized(projectvalueContent,"shorttitle",lang))}
         </div>
    </div>
[/#if]