[#-- Return le string avec les caractères spéciaux échappés  --]
[#function normalized input]
    [#local escapedString = input?url
    ?replace("%20", "_")
    ?replace("(", "_")
    ?replace(")", "_")]
    [#return escapedString]
[/#function]