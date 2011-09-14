<ul id="${element.eventTriggerId}" class="radioButtonGroup<#if element.styleClass?exists> ${element.styleClass}</#if>">
<#list options as option>
	<li>${option.render()} <label for="${option.id}" class="option ${option.styleClass!}<#if !element.enabled> disabled</#if>">${option.label}</label></li>
</#list>
</ul>