[#-------------- ASSIGNMENTS --------------]
[#assign contentNode = cmsfn.asJCRNode(content)]
[#assign newsId = ctx.getParameter('id')!?html]
[#if newsId??]
[#assign newsContent = cmsfn.contentById(newsId, "news")!]
[/#if]

[#-------------- RENDERING --------------]
<div class="container">

	[@cms.area name="newsOne" contextAttributes={"newsContent": newsContent}/]
    
</div>
