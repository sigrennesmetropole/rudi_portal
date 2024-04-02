[#-------------- ASSIGNMENTS --------------]
[#assign contentNode = cmsfn.asJCRNode(content)]
[#assign projectvalueResults = ctx.projectvalueResults!]

[#-------------- RENDERING  --------------]

[#if projectvalueResults?has_content && components?has_content]
        <div class="projectvalues-container">
        [#list projectvalueResults as projectvalueContent ]
                <div class="projectvalue-container-area">
                [#list components as component ]
                    [@cms.component content=component  contextAttributes={"projectvalueContent": projectvalueContent}/]
                [/#list]
                </div>
        [/#list]
        </div>
[/#if]

