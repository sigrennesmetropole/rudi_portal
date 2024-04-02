[#-------------- ASSIGNMENTS --------------]
[#assign contentNode = cmsfn.asJCRNode(content)]
[#assign newsResults = ctx.newsResults!]

[#-------------- RENDERING  --------------]

[#if newsResults?has_content && components?has_content]
        <div class="newss-container">
        [#list newsResults as newsContent ]
                <div class="news-container-area">
                [#list components as component ]
                    [@cms.component content=component  contextAttributes={"newsContent": newsContent}/]
                [/#list]
                </div>
        [/#list]
        </div>
[/#if]

