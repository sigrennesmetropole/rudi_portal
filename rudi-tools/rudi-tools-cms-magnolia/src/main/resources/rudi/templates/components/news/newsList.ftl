[#-------------- ASSIGNMENTS --------------]
[#assign contentNode = cmsfn.asJCRNode(content)]
[#assign newsResults = searchfn.searchContent("news", "*", "/rudi", "lib:news")! /]

[#-------------- RENDERING --------------]
<div class="container">

[#if newsResults?has_content]
        [@cms.area name="newsList" contextAttributes={"newsResults":newsResults} /]
[/#if]

</div>