[#-------------- ASSIGNMENTS --------------]
[#assign contentNode = cmsfn.asJCRNode(content)]
[#assign projectvalueId = ctx.getParameter('id')!?html]
[#if projectvalueId??]
[#assign projectvalueContent = cmsfn.contentById(projectvalueId, "projectvalues")!]
[/#if]

[#-------------- RENDERING --------------]
<div class="container">

	[@cms.area name="projectvalueOne" contextAttributes={"projectvalueContent": projectvalueContent}/]
    
</div>
