<@nav items=items />

<#macro nav items=[] level=0>
	<#if items?has_content>
		<ul<#if level == 0> id="menu"</#if>>
			<#list items as item>
				<li>
					<#if item.active>
						<div class="active">${item.label}</div>
					<#else>
						<a href="${item.link}"<#if item.childItems?has_content> class="expanded"</#if>>${item.label}</a>
					</#if>
					<@nav items=item.childItems level=level+1 />
				</li>
			</#list>
		</ul>
	</#if>
</#macro>