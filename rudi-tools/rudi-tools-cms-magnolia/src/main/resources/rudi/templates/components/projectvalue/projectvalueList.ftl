[#-------------- ASSIGNMENTS --------------]
[#assign contentNode = cmsfn.asJCRNode(content)]
[#assign projectvalueResults = searchfn.searchContent("projectvalues", "*", "/rudi", "lib:projectvalue")! /]

[#-------------- RENDERING --------------]
<div class="container">

[#if projectvalueResults?has_content]
        [@cms.area name="projectvalueList" contextAttributes={"projectvalueResults":projectvalueResults} /]
[/#if]

</div>