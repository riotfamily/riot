<ul id="${element.id}" class="checkboxGroup<#if element.styleClass?exists> ${element.styleClass}</#if>">
<#list options as option>
	<li>${option.render()} <label for="${option.id}" class="option">${option.label}</label></li>
</#list>
</ul>