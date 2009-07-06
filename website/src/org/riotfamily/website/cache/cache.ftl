<#---
  - @param key
  - @param bypass
  - @param ttl
  -->
<#assign block = cacheMacroHelper.blockDirective />

<#---
  - Discards the current CacheItem and its ancestors.
  -->
<#macro preventCaching>
    ${cacheMacroHelper.preventCaching()}
</#macro>

<#---
  - Tags the current CacheItem with the given tag
  - @param name The tag to assign
  -->
<#macro tag name>
	${cacheMacroHelper.tag(name)}
</#macro>