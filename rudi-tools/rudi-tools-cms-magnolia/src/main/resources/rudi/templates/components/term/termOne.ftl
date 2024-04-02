[#-------------- ASSIGNMENTS --------------]
[#assign contentNode = cmsfn.asJCRNode(content)]
[#assign termId = ctx.getParameter('id')!?html]
[#if termId??]
[#assign termContent = cmsfn.contentById(termId, "terms")!]
[/#if]

[#-------------- RENDERING --------------]
<div class="container">

	[@cms.area name="termOne" contextAttributes={"termContent": termContent}/]
    
</div>
