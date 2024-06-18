[#-------------- ASSIGNMENTS --------------]
[#include "/rudi/templates/macros/localized.ftl"/]
[#assign lang = ctx.getParameter('lang')!"fr"]
[#assign termContent = ctx.termContent!]

[#-------------- RENDERING --------------]

[#if termContent?has_content]
    <div class="term-container">
        <div class="title-detail">${localized(termContent,"title",lang)}</div>
    </div>
[/#if]