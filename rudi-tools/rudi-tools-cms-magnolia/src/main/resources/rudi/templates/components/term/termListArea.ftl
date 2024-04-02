[#-------------- ASSIGNMENTS --------------]
[#assign contentNode = cmsfn.asJCRNode(content)]
[#assign termResults = ctx.termResults!]

[#-------------- RENDERING  --------------]

[#if termResults?has_content && components?has_content]
        <div class="terms-container">
        [#list termResults as termContent ]   	
                <div class="term-container-area">
                [#list components as component ]
                    [@cms.component content=component  contextAttributes={"termContent": termContent}/]
                [/#list]
                </div>
        [/#list]
        </div>
[/#if]

