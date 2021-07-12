<#assign quantityNumber = quantity?number>

<html lang="en">
<body>
<div>
    ${seller}, ${quantity} of your ${product} <#if (quantityNumber > 1)>were<#else>was</#if> just sold to ${buyer}
    for ${price}.
</div>
</body>
</html>
