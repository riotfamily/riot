<#---
  - Macros to define and overwrite template blocks.
  - @namespace template
  -->

<#assign blocks = {} />
<#assign vars = {} />

<#---
  - Defines a template block that can be overwritten by child templates. 
  -->
<#macro block name>
	<#if childTemplate!false>
       <#local content><#nested /></#local>
       <#assign blocks = blocks + {name: content?trim} />
   <#else>
       <#if blockExists(name)>
           ${blocks[name]}
       <#else>
           <#nested>
       </#if>
   </#if>
</#macro>

<#---
  - Defines a child template that extends another template.
  -->
<#macro extend file>
	<#assign childTemplate=true />
	<#local swallow><#nested/></#local>
	<#assign childTemplate=false />
	<#include file>
</#macro>

<#function blockExists name>
	<#return (blocks[name]!)?has_content />
</#function>

<#macro set values...>
	<#list values?keys as name>
		<#if !vars[name]??><#assign vars = vars + {name: values[name] } /></#if>	
	</#list>
</#macro>