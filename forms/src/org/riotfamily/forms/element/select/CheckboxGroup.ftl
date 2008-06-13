<ul class="checkboxGroup<#if element.styleClass?exists> ${element.styleClass}</#if>">
<#list options as option>
	<li>${option.render()} <label for="${option.id}" class="option<#if !element.enabled> disabled</#if>">${option.label}</label></li>
</#list>
</ul>