[#-------------- ASSIGNMENTS --------------]
[#assign contentNode = cmsfn.asJCRNode(content)]
[#assign termResults = searchfn.searchContent("terms", "*", "/rudi", "lib:term")! /]

[#-------------- RENDERING --------------]
<div class="container">

[#if termResults?has_content]
        [@cms.area name="termList" contextAttributes={"termResults":termResults} /]
[/#if]

</div>