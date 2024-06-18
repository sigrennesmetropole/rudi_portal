[#-------------- ASSIGNMENTS --------------]
[#include "/rudi/templates/macros/localized.ftl"/]
[#include "/rudi/templates/macros/normalized.ftl"/]
[#assign lang = ctx.getParameter('lang')!"fr"]
[#assign newsContent = ctx.newsContent!]

[#-------------- RENDERING ----------------]
[#if newsContent?has_content]
    <div class="news-container">
        <div class="news-title1">${localized(newsContent,"title1",lang)}</div>
    </div>
[/#if]